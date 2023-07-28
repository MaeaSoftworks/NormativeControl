package com.maeasoftworks.core.enums

/**
 * Type of mistake
 *
 * todo: move translations to server/src/main/resources/i18n
 * @see com.maeasoftworks.core.model.MistakeInner
 * @see com.maeasoftworks.core.model.MistakeOuter
 * @param ru Russian translation of mistake cause
 */
enum class MistakeType(val ru: String) {
    // page size
    PAGE_WIDTH_IS_INCORRECT("Некорректная высота страницы"),
    PAGE_HEIGHT_IS_INCORRECT("Некорректная ширина страницы"),

    // page margins
    PAGE_MARGIN_TOP_IS_INCORRECT("Некорректный верхний отступ страницы"),
    PAGE_MARGIN_LEFT_IS_INCORRECT("Некорректный левый отступ страницы"),
    PAGE_MARGIN_BOTTOM_IS_INCORRECT("Некорректный нижний отступ страницы"),
    PAGE_MARGIN_RIGHT_IS_INCORRECT("Некорректный правый отступ страницы"),

    // chapters
    CHAPTER_NO_ONE_CHAPTER_FOUND("Не найдено ни одного раздела"),
    CHAPTER_UNDEFINED_CHAPTER("Неопознанная часть"),
    CHAPTER_EMPTY("Пустая часть"),
    CHAPTER_BODY_DISORDER("Основная часть находится на некорректной позиции"),

    // word errors
    WORD_GRAMMATICAL_ERROR("Грамматическая ошибка, которую нашел Word"),
    WORD_SPELL_ERROR("Ошибка правописания, которую нашел Word"),

    CHAPTER_FRONT_PAGE_NOT_FOUND("Раздел \"Титульный лист\" не найден"),
    CHAPTER_FRONT_PAGE_POSITION_MISMATCH("Раздел \"Титульный лист\": некорректная позиция"),
    CHAPTER_ANNOTATION_NOT_FOUND("Раздел \"Реферат\" не найден"),
    CHAPTER_ANNOTATION_POSITION_MISMATCH("Раздел \"Реферат\": некорректная позиция"),
    CHAPTER_CONTENTS_NOT_FOUND("Раздел \"Содержание\" не найден"),
    CHAPTER_CONTENTS_POSITION_MISMATCH("Раздел \"Содержание\": некорректная позиция"),
    CHAPTER_INTRODUCTION_NOT_FOUND("Раздел \"Введение\" не найден"),
    CHAPTER_INTRODUCTION_POSITION_MISMATCH("Раздел \"Введение\": некорректная позиция"),
    CHAPTER_BODY_NOT_FOUND("Раздел \"Основная часть\" не найден"),
    CHAPTER_BODY_POSITION_MISMATCH("Раздел \"Основная часть\": некорректная позиция"),
    CHAPTER_CONCLUSION_NOT_FOUND("Раздел \"Заключение\" не найден"),
    CHAPTER_CONCLUSION_POSITION_MISMATCH("Раздел \"Заключение\": некорректная позиция"),
    CHAPTER_REFERENCES_NOT_FOUND("Раздел \"Список литературы\" не найден"),
    CHAPTER_REFERENCES_POSITION_MISMATCH("Раздел \"Список литературы\": некорректная позиция"),
    CHAPTER_APPENDIX_NOT_FOUND("Раздел \"Приложение\" не найден"),
    CHAPTER_APPENDIX_POSITION_MISMATCH("Раздел \"Приложение\": некорректная позиция"),

    TODO_ERROR("Неопределенная ошибка (будет обработана позже)"),

    TEXT_HEADER_EMPTY("Пустой заголовок раздела"),
    TEXT_HEADER_ALIGNMENT("Некорректное выравнивание заголовка раздела"),
    TEXT_HEADER_BODY_ALIGNMENT("Некорректное выравнивание заголовка/подзаголовка основной части"),
    TEXT_REGULAR_INCORRECT_ALIGNMENT("Некорректное выравнивание текста"),
    TEXT_WHITESPACE_ALIGNMENT("Некорректное выравнивание пустой строки"),
    TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT("Некорректное выравнивание пустой строки после заголовка"),
    TEXT_HEADER_NOT_UPPERCASE("Заголовок раздела написан не строчными буквами"),
    TEXT_HEADER_BODY_UPPERCASE("Заголовок основной части написан строчными буквами"),
    TEXT_REGULAR_UPPERCASE("Текст написан строчными буквами"),
    TEXT_WHITESPACE_UPPERCASE("Пустая строка отформатирована в режиме только строчных букв"),
    TEXT_WHITESPACE_AFTER_HEADER_UPPERCASE("Пустая строка после заголовка отформатирована в режиме только строчных букв"),
    TEXT_HEADER_NOT_BOLD("Не полужирный заголовок"),
    TEXT_REGULAR_WAS_BOLD("Полужирный текст"),
    TEXT_WHITESPACE_BOLD("Полужирная пустая строка"),
    TEXT_WHITESPACE_AFTER_HEADER_BOLD("Полужирная пустая строка после заголовка"),
    TEXT_COMMON_FONT("Некорректный шрифт текста"),
    TEXT_WHITESPACE_FONT("Некорректный шрифт пустой строки"),
    TEXT_COMMON_TEXT_COLOR("Некорректный цвет текста"),
    TEXT_WHITESPACE_TEXT_COLOR("Некорректный цвет пустой строки"),
    TEXT_COMMON_UNDERLINED("Текст подчеркнут"),
    TEXT_WHITESPACE_UNDERLINED("Пустая строка подчеркнута"),
    TEXT_COMMON_INCORRECT_FONT_SIZE("Некорректный размер шрифта текста"),
    TEXT_WHITESPACE_INCORRECT_FONT_SIZE("Некорректный размер шрифта пустой строки"),
    TEXT_COMMON_ITALIC_TEXT("Найден курсивный текст"),
    TEXT_WHITESPACE_ITALIC("Найден курсивная пустая строка"),
    TEXT_COMMON_STRIKETHROUGH("Найден перечеркнутый текст"),
    TEXT_WHITESPACE_STRIKETHROUGH("Найдена перечеркнутая пустая строка"),
    TEXT_COMMON_HIGHLIGHT("Найден текст с выделением"),
    TEXT_WHITESPACE_HIGHLIGHT("Найдена пустая строка с выделением"),
    TEXT_COMMON_BORDER("Найдена рамка у параграфа"),
    TEXT_WHITESPACE_BORDER("Найдена рамка у пустого параграфа"),
    TEXT_COMMON_BACKGROUND_FILL("Найдена заливка фона у текста"),
    TEXT_WHITESPACE_BACKGROUND_FILL("Найдена заливка фона у пустой строки"),
    TEXT_HEADER_LINE_SPACING("Некорректный межстрочный интервал заголовка"),
    TEXT_REGULAR_LINE_SPACING("Некорректный межстрочный интервал текста"),
    TEXT_WHITESPACE_LINE_SPACING("Некорректный межстрочный интервал пустой строки"),
    TEXT_REGULAR_INDENT_FIRST_LINES("Некорректный отступ первой строки параграфа"),
    TEXT_HEADER_INDENT_FIRST_LINES("Некорректный отступ первой строки заголовка параграфа"),
    TEXT_WHITESPACE_INDENT_FIRST_LINES("Некорректный отступ первой строки пустого параграфа"),
    TEXT_COMMON_INDENT_LEFT("Некорректный левый отступ параграфа"),
    TEXT_WHITESPACE_INDENT_LEFT("Некорректный левый отступ пустого параграфа"),
    TEXT_COMMON_INDENT_RIGHT("Некорректный правый отступ параграфа"),
    TEXT_WHITESPACE_INDENT_RIGHT("Некорректный правый отступ пустого параграфа"),
    TEXT_COMMON_RUN_SPACING("Некорректный межсимвольный интервал текста"),
    TEXT_WHITESPACE_RUN_SPACING("Некорректный межсимвольный интервал пустой строки"),
    TEXT_HEADER_REDUNDANT_DOT("Точка на конце заголовка"),
    TEXT_COMMON_USE_FIRST_LINE_INDENT_INSTEAD_OF_TAB("Используйте отступ первой строки вместо табуляции"),
    TEXT_HEADER_AUTO_HYPHEN("Не отключен автоматический перенос слов в заголовке"),
    TEXT_HYPERLINK_WARNING("Найдена гиперссылка. Убедитесь, что есть острая необходимость в ее наличии."),

    DOCUMENT_UNEXPECTED_CONTENT("Неопознанный контент на уровне параграфа"),
    PARAGRAPH_UNEXPECTED_CONTENT("Неопознанный контент на уровне рана"),
    RUN_UNEXPECTED_CONTENT("Неопознанный контент на уровне текста"),

    // body
    TEXT_BODY_SUBHEADER_NUMBER_ORDER_MISMATCH("Неверный порядок нумерации подразделов"),
    TEXT_BODY_SUBHEADER_LEVEL_WAS_MORE_THAN_3("Уровень вложенности подразделов больше 3"),
    TEXT_BODY_SUBHEADER_WAS_EMPTY("Подзаголовок был пустым"),

    // header only settings
    TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED("Не найдена пустая строка после заголовка"),
    TEXT_HYPERLINKS_NOT_ALLOWED_HERE("Ссылки запрещены в данном разделе"),

    // pictures
    PICTURE_IS_NOT_INLINED("Изображение не встроено в текст"),
    PICTURE_REQUIRED_BLANK_LINE_BEFORE_PICTURE("Необходима пустая строка перед изображением"),
    PICTURE_REQUIRED_BLANK_LINE_AFTER_PICTURE_TITLE("Необходима пустая строка после подписи изображения"),
    PICTURE_TITLE_NUMBER_DISORDER("Неверный порядок нумерации изображений"),
    PICTURE_TITLE_WRONG_FORMAT("Неверный формат подписи изображений"),
    PICTURE_TITLE_REQUIRED_LINE_BREAK_BETWEEN_PICTURE_AND_TITLE("Необходим перенос строки между изображением и подписью к нему"),
    PICTURE_TITLE_NOT_CENTERED("Некорректное выравнивание подписи к изображению"),
    PICTURE_TITLE_ENDS_WITH_DOT("Точка в конце подписи к изображению"),

    // lists
    LIST_LEVEL_MORE_THAN_2("Уровень вложенности списка больше 2"),
    ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_1("Некорректный формат маркера первого уровня нумерованного списка"),
    ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_2("Некорректный формат маркера второго уровня нумерованного списка"),
    ORDERED_LIST_WRONG_LETTER("Данная буква не должна использоваться как маркер элемента нумерованного списка"),
    ORDERED_LIST_INCORRECT_MARKER_FORMAT("Некорректный формат маркера ненумерованного списка"),

    TEXT_ABANDONED_ABBREVIATION_FOUND("Сокращения \"к-рый\", \"ур-ие\", \"вм.\", \"напр.\", \"м.б.\" запрещены")
}
