package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.enums.ChapterType
import com.maeasoftworks.docx4nc.enums.ChapterType.*
import com.maeasoftworks.docx4nc.enums.MistakeType
import com.maeasoftworks.docx4nc.enums.MistakeType.*
import com.maeasoftworks.docx4nc.enums.Status
import com.maeasoftworks.docx4nc.model.*
import com.maeasoftworks.docx4nc.utils.ignore
import org.docx4j.jaxb.Context
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.openpackaging.packages.ProtectDocument
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart
import org.docx4j.wml.Comments
import org.docx4j.wml.P
import org.docx4j.wml.R
import org.docx4j.wml.STDocProtect
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.util.UUID

/**
 * Класс, ответственный за парсинг всего документа и делегирование задач на парсинг отдельных
 * частей более специализированным парсерам
 *
 * @author prmncr
 */
class DocumentParser(val documentData: DocumentData, private var password: String) {

    /**
     * Буффер для текстового содержимого документа
     *
     * @author prmncr
     */
    val texts = Texts()

    /**
     * Объект, хранящий в себе содержимое документа
     *
     * @author prmncr
     */
    lateinit var doc: MainDocumentPart

    /**
     * Объект для получения свойств параграфов и ранов
     *
     * @author prmncr
     */
    lateinit var resolver: Resolver

    /**
     * Объект, позволяющий создавать сущности, которые можно включить в doc
     *
     * @author prmncr
     */
    private val factory = Context.getWmlObjectFactory()

    /**
     * Объект, позволяющий перевести файл .docx в объект, и получить из него содержимое
     *
     * @author prmncr
     */
    private lateinit var mlPackage: WordprocessingMLPackage

    var numbering: NumberingDefinitionsPart? = null
    private var comments: CommentsPart? = null

    var chapters: MutableList<Chapter> = ArrayList()
    var parsers: MutableList<ChapterParser> = ArrayList()
    var mistakes: MutableList<MistakeOuter> = ArrayList()
    val pictures: MutableList<Picture> = ArrayList()

    private var mistakeId: Long = 0

    /**
     * Метод добавляет ошибку в документ
     *
     * На основе полученных аргументов конструирует объект типа MistakeOuter и добавляет
     * её в список ошибок документа mistakes
     *
     * @param type тип ошибки, объект из перечисления MistakeType
     * @param p параграф, в котором найдена ошибка
     * @param r прогон, в котором найдена ошибка
     * @param description текстовое описание ошибки вида "найдено X ожидалось Y"
     *
     * @author prmncr
     */
    fun addMistake(type: MistakeType, p: Int? = null, r: Int? = null, description: String? = null) {
        mistakes.add(MistakeOuter(mistakeId++, p, r, type, description))
    }

    /**
     * Метод получает MistakeInner и используя её данные вызывает addMistake,
     * с помощью которого она преобразуется в MistakeOuter И добавляется в список ошибок документа
     *
     * @param type тип ошибки, объект из перечисления MistakeType
     * @param p параграф, в котором найдена ошибка
     * @param r прогон, в котором найдена ошибка
     * @param description текстовое описание ошибки вида "найдено X ожидалось Y"
     *
     * @author prmncr
     */
    fun addMistake(mistake: MistakeInner?) {
        if (mistake != null) {
            addMistake(mistake.mistakeType, mistake.p, mistake.r, mistake.description)
        }
    }

    /**
     * Метод инициирует поля и выполняет прочие действия, необходимые для нормальной эксплуатации
     * созданного объекта:
     * - Создаёт объект на основе .docx файла для доступа к его содержимому
     * - Получает из него текстовое содержимое документа
     * - Получает буфер для записи комментариев, в который будут заноситься текстовые представления ошибок
     * - Получает Id последней внесённой в документ ошибки
     * - Получает нумерацию страниц документа
     *
     * @author prmncr
     */
    fun init() {
        try {
            mlPackage = WordprocessingMLPackage.load(ByteArrayInputStream(documentData.file))
            doc = mlPackage.mainDocumentPart
            doc.contents.body
            resolver = Resolver(PropertyResolver(mlPackage))
            comments = doc.commentsPart
            if (comments == null) {
                comments = CommentsPart().also { it.jaxbElement = factory.createComments() }
                doc.addTargetPart(comments)
            }
            mistakeId = comments!!.jaxbElement.comment.size.toLong()
            numbering = doc.numberingDefinitionsPart
        } catch (e: Docx4JException) {
            documentData.status = Status.ERROR
            documentData.failureType = FailureType.FILE_READING_ERROR
        }
    }

    /**
     * Метод, ответственный за организацию поиска ошибок в документе
     *
     * Он делает:
     * - Поиск ошибок в размере страниц
     * - Поиск ошибок в отступах
     * - Определение глав и поиск ошибок в них
     * - Создаёт парсеры для разных типов страниц и поочерёдно активирует их
     * - Проверяет очерёдность картинок в документе
     *
     * @author prmncr
     */
    fun runVerification() {
        verifyPageSize()
        verifyPageMargins()
        setupChapters()
        createParsers()
        for (parser in parsers) {
            parser.parse()
        }
        checkPicturesOrder(AnonymousParser(this), 0, true, pictures)
    }

    /**
     * Метод добавляет комментарии с текстом ошибок в документ, запрещает дальнейшее его изменение без пароля,
     * и сохраняет всё это
     *
     * @author prmncr
     */
    fun addCommentsAndSave() {
        addComments()
        lock()
        save()
    }

    /**
     * Сохраняет изменения, внесённые в содержимое документа в его объекте, в сам документ
     *
     * @author prmncr
     */
    private fun save() {
        val stream = ByteArrayOutputStream()
        mlPackage.save(stream)
        documentData.file = stream.toByteArray()
    }

    /**
     * Запрещает изменения документа без ввода пароля
     *
     * @author prmncr
     */
    private fun lock() {
        ProtectDocument(mlPackage).restrictEditing(STDocProtect.READ_ONLY, password)
    }

    /**
     * Добавляет в документ комментарии с текстом ошибок и привязкой их к параграфам и прогонам
     *
     * Берёт список ошибок, сортирует его по номеру параграфа и по номеру прогона по-возрастанию, реверсирует его,
     * получая список по-убыванию, затем проходится по нему циклом for-each, на основе каждой ошибки создаёт
     * комментарий
     * - Если у ошибки нет параграфа - комментарий добавляется в начале документа без привязки к параграфу или прогону
     * - Если у ошибки нет прогона - комментарий привязывается к параграфу
     * - Если у ошибки есть параграф и прогон - комментарий привязывается к параграфу и прогону
     *
     * @author prmncr
     */
    private fun addComments() {
        val errors = mistakes.asIterable().sortedWith(compareBy({ it.p }, { it.r })).reversed().toList()
        for (mistake in errors) {
            val comment = createComment(
                mistake.mistakeId,
                mistake.mistakeType.ru.let { x ->
                    if (mistake.description != null) {
                        return@let x + ": ${
                            mistake.description.split('/')
                                .let { if (it.size > 1) "найдено: ${it[0]}, ожидалось: ${it[1]}" else it[0] }
                        }"
                    } else {
                        return@let x
                    }
                }
            )
            comments!!.jaxbElement.comment.add(comment)
            val commentRangeStart = factory.createCommentRangeStart().also {
                it.id = BigInteger.valueOf(mistake.mistakeId)
            }
            val commentRangeEnd = factory.createCommentRangeEnd().also { it.id = BigInteger.valueOf(mistake.mistakeId) }
            if (mistake.p == null) {
                val paragraph = doc.content[0] as P
                paragraph.content.add(commentRangeStart)
                paragraph.content.add(commentRangeEnd)
                paragraph.content.add(createRunCommentReference(mistake.mistakeId))
            } else if (mistake.r == null) {
                val paragraph: P = try {
                    doc.content[mistake.p] as P
                } catch (e: ClassCastException) {
                    factory.createP().apply { doc.content.add(mistake.p + 1, this) }
                }
                paragraph.content.add(0, commentRangeStart)
                paragraph.content.add(commentRangeEnd)
                paragraph.content.add(createRunCommentReference(mistake.mistakeId))
            } else {
                val paragraph: P = try {
                    doc.content[mistake.p] as P
                } catch (e: ClassCastException) {
                    factory.createP().apply { doc.content.add(mistake.p + 1, this) }
                }
                paragraph.content.add(if (mistake.r == 0) 0 else mistake.r - 1, commentRangeStart)
                if (mistake.r == paragraph.content.size - 1) {
                    paragraph.content.add(commentRangeEnd)
                    paragraph.content.add(createRunCommentReference(mistake.mistakeId))
                } else {
                    paragraph.content.add(mistake.r + 1, commentRangeEnd)
                    paragraph.content.add(mistake.r + 2, createRunCommentReference(mistake.mistakeId))
                }
            }
        }
    }

    /**
     * Производит установку глав:
     * - Находит главы в документе, разбивая документ на части с помощью заголовков
     * - Определяет тип каждой главы и удаляет из списка на обработку пустые (заголовок есть, содержимого нет),
     * при этом добавляя соответствующие ошибки в документ
     * - Проверяет, что главы идут в правильном порядке, указанном ГОСТ'ом:
     * (frontPage, annotation, contents, introduction, body, conclusion, references, appendix)
     * - Проверяет, что главы body идут в правильном порядке
     *
     * @author prmncr
     */
    fun setupChapters() {
        findChapters()
        detectChapters()
        verifyChapters()
        verifyBody()
    }

    /**
     * Проверяет ширину и высоту страницы
     * Если ширина и/или высота не соответствуют необходимым - добавляет соотв. ошибку в документ
     *
     * @author prmncr
     */
    fun verifyPageSize() {
        val pageSize = doc.contents.body.sectPr.pgSz
        if (pageSize.w.intValueExact() != 11906) {
            addMistake(PAGE_WIDTH_IS_INCORRECT)
        }
        if (pageSize.h.intValueExact() != 16838) {
            addMistake(PAGE_HEIGHT_IS_INCORRECT)
        }
    }

    /**
     * Проверяет отступы на странице на соответствие ГОСТ'ам
     * В случае нахождения несоответствия отступа слева, справа, сверху или снизу - добавляет соответствующую
     * ошибку в документ
     *
     * @author prmncr
     */
    fun verifyPageMargins() {
        val pageMargins = doc.contents.body.sectPr.pgMar
        if (pageMargins.top.intValueExact() != 1134) {
            addMistake(PAGE_MARGIN_TOP_IS_INCORRECT)
        }
        if (pageMargins.right.intValueExact() != 850) {
            addMistake(PAGE_MARGIN_RIGHT_IS_INCORRECT)
        }
        if (pageMargins.bottom.intValueExact() != 1134) {
            addMistake(PAGE_MARGIN_BOTTOM_IS_INCORRECT)
        }
        if (pageMargins.left.intValueExact() != 1701) {
            addMistake(PAGE_MARGIN_LEFT_IS_INCORRECT)
        }
    }

    /**
     * Находит главы в документе, ориентируясь по заголовкам
     *
     * Если под заголовком нет содержимого - такая глава удаляется из списка рассматриваемых глав
     *
     * @author prmncr
     */
    fun findChapters() {
        val paragraphs = doc.content
        var paragraph = 0
        var sectorId = 0
        while (paragraph < paragraphs.size) {
            if (paragraphs[paragraph] is P && isHeader(paragraph, 1)) {
                sectorId++
                for (i in chapters.size..sectorId) {
                    chapters.add(Chapter(paragraph))
                }
                chapters[sectorId].header = paragraphs[paragraph] as P
            }
            if (chapters.size <= sectorId) {
                chapters.add(Chapter(paragraph))
            }
            chapters[sectorId].add(paragraphs[paragraph])
            paragraph++
        }
        if (!chapters[0].hasHeader && chapters[0].content.size == 0) {
            chapters.removeAt(0)
        }
    }

    /**
     * Определяет тип каждой главы, присваеивает ей его, и удаляет из списка на обработку пустые
     * (заголовок есть, содержимого нет) главы (кроме тех, чей тип был определён как frontpage),
     * при этом добавляя соответствующие ошибки в документ
     *
     * @author prmncr
     */
    fun detectChapters() {
        val emptyChapters = ArrayList<Int>()
        for (chapter in 0 until chapters.size) {
            if (chapters[chapter][0] is P) {
                if (!chapters[chapter].hasHeader) {
                    chapters[chapter].type = FRONT_PAGE
                    continue
                }
                val text = texts.getText(chapters[chapter].header)
                if (text.isEmpty()) {
                    emptyChapters += chapter
                    addMistake(TEXT_HEADER_EMPTY)
                    continue
                }
                chapters[chapter].type = detectNodeType(text, chapters[chapter].startPos)
            }
        }
        if (!chapters[0].isChapterDetected) {
            chapters[0].type = FRONT_PAGE
        }
        for (empty in emptyChapters) {
            chapters.removeAt(empty)
        }
    }

    /**
     * Определяет тип каждой главы по переданным параметрам
     *
     * @param text текстовое содержимое главы
     * @param startPos место, в котором в документе начинается данная глава
     *
     * @author prmncr
     */
    private fun detectNodeType(text: String, startPos: Int): ChapterType {
        if (text.split(Regex("\\s+"))[0].matches(Regex("^(?:\\d{1,2}\\.?){1,3}$"))) {
            return BODY
        }
        for (keys in 0 until ChapterMarkers.markers.size) {
            for (key in ChapterMarkers.markers[keys]) {
                if (text.uppercase().contains(key)) {
                    return ChapterType.values()[keys]
                }
            }
        }
        for (keyword in ChapterMarkers.appendix) {
            if (text.uppercase().startsWith(keyword)) {
                return APPENDIX
            }
        }
        addMistake(CHAPTER_UNDEFINED_CHAPTER, startPos)
        return UNDEFINED
    }

    /**
     * Проверяет, что глава стоит на нужном месте и имеет нужный тип и порядок
     *
     * @param pos позиция, на которой находится начало главы
     * @param type тип главы
     * @param types типы всех глав в документе
     * @param notFound ошибка, которую надо добавить, если глава не будет найдена по адресу
     * @param mismatch ошибка, которую надо добавить, если глава будет иметь неверный тип
     *
     * @author prmncr
     */
    private fun verifyChapter(
        pos: Int,
        type: ChapterType,
        types: List<ChapterType?>,
        notFound: MistakeType,
        mismatch: MistakeType
    ) = ignore<IndexOutOfBoundsException> {
        if (type !in types) {
            addMistake(notFound)
        } else if (chapters[pos].type != type) {
            addMistake(mismatch)
        }
    }

    /**
     * Содержит порядок, в котором должны располагаться главы в документе, какой тип они должны иметь, и осуществляет
     * проверку по этим параметрам и добавление ошибок
     *
     * @author prmncr
     */
    fun verifyChapters() {
        val t = chapters.map { it.type }
        if (chapters.size == 0) {
            addMistake(CHAPTER_NO_ONE_CHAPTER_FOUND)
        }
        verifyChapter(0, FRONT_PAGE, t, CHAPTER_FRONT_PAGE_NOT_FOUND, CHAPTER_FRONT_PAGE_POSITION_MISMATCH)
        verifyChapter(1, ANNOTATION, t, CHAPTER_ANNOTATION_NOT_FOUND, CHAPTER_ANNOTATION_POSITION_MISMATCH)
        verifyChapter(2, CONTENTS, t, CHAPTER_CONTENTS_NOT_FOUND, CHAPTER_CONTENTS_POSITION_MISMATCH)
        verifyChapter(3, INTRODUCTION, t, CHAPTER_INTRODUCTION_NOT_FOUND, CHAPTER_INTRODUCTION_POSITION_MISMATCH)
        verifyChapter(4, BODY, t, CHAPTER_BODY_NOT_FOUND, CHAPTER_BODY_POSITION_MISMATCH)
        var i = 4
        ignore<IndexOutOfBoundsException> {
            while (chapters[i].type == BODY) {
                i++
            }
        }
        verifyChapter(i, CONCLUSION, t, CHAPTER_CONCLUSION_NOT_FOUND, CHAPTER_CONCLUSION_POSITION_MISMATCH)
        verifyChapter(i + 1, REFERENCES, t, CHAPTER_REFERENCES_NOT_FOUND, CHAPTER_REFERENCES_POSITION_MISMATCH)
        verifyChapter(i + 2, APPENDIX, t, CHAPTER_APPENDIX_NOT_FOUND, CHAPTER_APPENDIX_POSITION_MISMATCH)
    }

    /**
     * Добавляет нужный парсер на каждый тип главы, присутствующий в документе
     *
     * @author prmncr
     */
    fun createParsers() {
        for (chapter in chapters) {
            when (chapter.type) {
                FRONT_PAGE -> parsers.add(FrontPageParser(chapter, this))
                ANNOTATION -> parsers.add(SimpleParser(chapter, this))
                CONTENTS -> parsers.add(ContentsParser(chapter, this))
                INTRODUCTION -> parsers.add(SimpleParser(chapter, this))
                BODY -> parsers.add(BodyParser(chapter, this))
                CONCLUSION -> parsers.add(ConclusionParser(chapter, this))
                REFERENCES -> parsers.add(ReferencesParser(chapter, this))
                APPENDIX -> parsers.add(AppendixParser(chapter, this))
                else -> {}
            }
        }
    }

    /**
     * Проверяет, что главы в body идут в верном порядке
     *
     * @author prmncr
     */
    private fun verifyBody() {
        var i = 0
        chapters.filter { it.type == BODY }.forEach {
            if (!texts.getText(it.header).startsWith((++i).toString())) {
                addMistake(CHAPTER_BODY_DISORDER)
            }
        }
    }

    /**
     * Проверяет, содержит ли параграф в себе заголовок
     *
     * @param paragraph номер параграфа
     * @param level уровень вложенности параграфа
     *
     * @author prmncr
     */
    fun isHeader(paragraph: Int, level: Int? = null): Boolean {
        val pPr = resolver.getEffectivePPr(doc.content[paragraph] as P)
        if (pPr.outlineLvl == null) {
            return false
        }
        return if (level != null) {
            pPr.outlineLvl.`val`.toInt() == level - 1
        } else {
            pPr.outlineLvl.`val` != null
        }
    }

    /**
     * Проверяет, идут ли картинки по порядку
     *
     * @param context парсер для обработки картинок
     * @param level уровень вложенности картинки
     * @param useInnerIndexer использовать индексацию внутри главы или по документу?
     * @param container коллекция объектов картинок
     *
     * @author prmncr
     */
    fun checkPicturesOrder(
        context: ChapterParser,
        level: Int,
        useInnerIndexer: Boolean,
        container: MutableList<Picture>
    ) {
        if (useInnerIndexer) {
            var index = 1
            for (picture in container) {
                if (picture.title == null) {
                    continue
                }
                val match = context.pictureTitleMatcher(picture.title!!)
                if (match != null) {
                    if (match.groups[1 + level]!!.value.toInt() != index) {
                        addMistake(PICTURE_TITLE_NUMBER_DISORDER, picture.p, picture.r)
                    }
                    context.validatePictureTitleStyle(picture.p)
                } else {
                    addMistake(PICTURE_TITLE_WRONG_FORMAT, picture.p, picture.r)
                }
                index++
            }
        } else {
            context.root.pictures.addAll(container)
        }
    }

    /**
     * Создаёт комментарий
     *
     * @param commentId порядковый номер комментарий
     * @param message текст комментария
     *
     * @author prmncr
     */
    private fun createComment(commentId: Long, message: String): Comments.Comment {
        return factory.createCommentsComment().also { comment ->
            comment.id = BigInteger.valueOf(commentId)
            comment.author = "normative control"
            comment.content.add(
                factory.createP().also { p ->
                    p.paraId = "nc-${UUID.randomUUID()}"
                    p.content.add(
                        factory.createR().also { r ->
                            r.content.add(factory.createText().also { text -> text.value = message })
                            r.rPr = factory.createRPr().also {
                                it.rFonts = factory.createRFonts()
                                it.rFonts.ascii = "Consolas"
                                it.rFonts.cs = "Consolas"
                                it.rFonts.eastAsia = "Consolas"
                                it.rFonts.hAnsi = "Consolas"
                            }
                        }
                    )
                }
            )
        }
    }

    /**
     * Создаёт ссылку на комментарий, привязанную к прогону
     *
     * @param commentId порядковый номер комментарий
     *
     * @author prmncr
     */
    private fun createRunCommentReference(commentId: Long): R {
        return factory.createR().also { run ->
            run.content.add(
                factory.createRCommentReference().also {
                    it.id = BigInteger.valueOf(commentId)
                }
            )
        }
    }
}
