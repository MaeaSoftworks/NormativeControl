package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.dtos.enums.ChapterType
import com.maeasoftworks.normativecontrol.dtos.enums.ErrorType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
class ChapterTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "chapter"

    @Test
    fun `all chapters detected`() {
        val parser = base.createParser(directory, "correctSectors.docx")
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()
        Assert.isTrue(parser.chapters.size == 9, "Expected: 9 nodes\nFound: ${parser.chapters.size}")
        Assert.isTrue(
            parser.chapters[0].type == ChapterType.FRONT_PAGE,
            "Expected: FRONT_PAGE\nFound: ${parser.chapters[0].type}"
        )
        Assert.isTrue(
            parser.chapters[1].type == ChapterType.ANNOTATION,
            "Expected: ANNOTATION\nFound: ${parser.chapters[1].type}"
        )
        Assert.isTrue(
            parser.chapters[2].type == ChapterType.CONTENTS,
            "Expected: CONTENTS\nFound: ${parser.chapters[2].type}"
        )
        Assert.isTrue(
            parser.chapters[3].type == ChapterType.INTRODUCTION,
            "Expected: INTRODUCTION\nFound: ${parser.chapters[3].type}"
        )
        Assert.isTrue(
            parser.chapters[4].type == ChapterType.BODY,
            "Expected: BODY1\nFound: ${parser.chapters[4].type}"
        )
        Assert.isTrue(
            parser.chapters[5].type == ChapterType.BODY,
            "Expected: BODY2\nFound: ${parser.chapters[5].type}"
        )
        Assert.isTrue(
            parser.chapters[6].type == ChapterType.CONCLUSION,
            "Expected: CONCLUSION\nFound: ${parser.chapters[6].type}"
        )
        Assert.isTrue(
            parser.chapters[7].type == ChapterType.REFERENCES,
            "Expected: REFERENCES\nFound: ${parser.chapters[7].type}"
        )
        Assert.isTrue(
            parser.chapters[8].type == ChapterType.APPENDIX,
            "Expected: APPENDIX\nFound: ${parser.chapters[8].type}"
        )
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun `missing chapter reported`() {
        val parser = base.createParser(directory, "skippedSector.docx")
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()
        Assert.isTrue(parser.chapters.size == 7, "2 nodes must be found!")
        Assert.isTrue(parser.chapters[0].type == ChapterType.FRONT_PAGE, "FRONT_PAGE must be found!")
        Assert.isTrue(parser.chapters[1].type == ChapterType.ANNOTATION, "ANNOTATION must be found!")
        Assert.isTrue(parser.chapters[2].type == ChapterType.CONTENTS, "CONTENTS must be found!")
        Assert.isTrue(parser.chapters[3].type == ChapterType.INTRODUCTION, "INTRODUCTION must be found!")
        Assert.isTrue(parser.chapters[4].type == ChapterType.CONCLUSION, "CONCLUSION must be found!")
        Assert.isTrue(parser.chapters[5].type == ChapterType.REFERENCES, "REFERENCES must be found!")
        Assert.isTrue(parser.chapters[6].type == ChapterType.APPENDIX, "APPENDIX must be found!")
        Assert.isTrue(parser.errors.size == 1, "There should be error!")
        Assert.isTrue(
            parser.errors[0].errorType == ErrorType.CHAPTER_BODY_NOT_FOUND,
            "There should be CHAPTER_BODY_NOT_FOUND!"
        )
    }

    @Test
    fun `all missing chapters reported`() {
        val parser = base.createParser(directory, "skippedAllSectors.docx")
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()
        Assert.isTrue(parser.chapters.size == 2, "2 nodes must be found!")
        Assert.isTrue(parser.chapters[0].type == ChapterType.FRONT_PAGE, "FRONT_PAGE must be found!")
        Assert.isTrue(parser.chapters[1].type == ChapterType.APPENDIX, "APPENDIX must be found!")
        Assert.isTrue(parser.errors.size == 6, "Expected: 6 errors\nFound: ${parser.errors.size}")
        Assert.isTrue(
            parser.errors[0].errorType == ErrorType.CHAPTER_ANNOTATION_NOT_FOUND,
            "There should be CHAPTER_ANNOTATION_NOT_FOUND!"
        )
        Assert.isTrue(
            parser.errors[1].errorType == ErrorType.CHAPTER_CONTENTS_NOT_FOUND,
            "There should be CHAPTER_CONTENTS_NOT_FOUND!"
        )
        Assert.isTrue(
            parser.errors[2].errorType == ErrorType.CHAPTER_INTRODUCTION_NOT_FOUND,
            "There should be CHAPTER_INTRODUCTION_NOT_FOUND!"
        )
        Assert.isTrue(
            parser.errors[3].errorType == ErrorType.CHAPTER_BODY_NOT_FOUND,
            "There should be CHAPTER_BODY_NOT_FOUND!"
        )
        Assert.isTrue(
            parser.errors[4].errorType == ErrorType.CHAPTER_CONCLUSION_NOT_FOUND,
            "There should be CHAPTER_CONCLUSION_NOT_FOUND!"
        )
        Assert.isTrue(
            parser.errors[5].errorType == ErrorType.CHAPTER_REFERENCES_NOT_FOUND,
            "There should be CHAPTER_REFERENCES_NOT_FOUND!"
        )
    }

    @Test
    fun `full document chapters detected properly`() {
        val parser = base.createParser(directory, "general/full test 1.docx", true)
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()
        Assert.isTrue(parser.chapters.size == 12, "12 nodes must be found!")
        Assert.isTrue(parser.errors.size == 0, "Expected: 0 errors\nFound: ${parser.errors.size}")
    }
}