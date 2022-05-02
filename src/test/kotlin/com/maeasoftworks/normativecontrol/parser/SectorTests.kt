package com.maeasoftworks.normativecontrol.parser

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
internal class SectorTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "sectors"

    @Test
    fun correctSectorsFound() {
        val parser = base.createParser(directory, "correctSectors.docx")
        parser.findSectors()
        parser.detectNodes()
        Assert.notEmpty(parser.nodes[0].content, "0 not found!")
        Assert.notEmpty(parser.nodes[1].content, "1 not found!")
        Assert.notEmpty(parser.nodes[2].content, "2 not found!")
        Assert.notEmpty(parser.nodes[3].content, "3 not found!")
        Assert.notEmpty(parser.nodes[4].content, "4 not found!")
        Assert.notEmpty(parser.nodes[5].content, "5 not found!")
        Assert.notEmpty(parser.nodes[6].content, "6 not found!")
        Assert.notEmpty(parser.nodes[7].content, "7 not found!")
        Assert.notEmpty(parser.nodes[8].content, "8 not found!")
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun incorrectSectorsFound() {
        val parser = base.createParser(directory, "skippedSector.docx")
        parser.findSectors()
        parser.detectNodes()
        parser.findIncorrectNodes()
        Assert.isTrue(parser.nodes.size == 7, "2 sectors must be found!")
        Assert.notEmpty(parser.nodes[0].content, "0 must be found!")
        Assert.notEmpty(parser.nodes[1].content, "1 must be found!")
        Assert.notEmpty(parser.nodes[2].content, "2 must be found!")
        Assert.notEmpty(parser.nodes[3].content, "3 must be found!")
        Assert.notEmpty(parser.nodes[4].content, "4 must be found!")
        Assert.notEmpty(parser.nodes[5].content, "5 must be found!")
        Assert.notEmpty(parser.nodes[6].content, "6 must be found!")
        Assert.isTrue(parser.errors.size == 1, "There should be error!")
    }

    @Test
    fun allSectorsSkipped() {
        val parser = base.createParser(directory, "skippedAllSectors.docx")
        parser.findSectors()
        parser.detectNodes()
        parser.findIncorrectNodes()
        Assert.isTrue(parser.nodes.size == 2, "2 sectors must be found!")
        Assert.notEmpty(parser.nodes[0].content, "0 must be found!")
        Assert.notEmpty(parser.nodes[1].content, "1 must be found!")
        Assert.isTrue(parser.errors.size > 0, "There should be error!")
    }

    @Test
    fun headerDetectedWithoutLineBreak() {
        val parser = base.createParser(directory, "headerWithoutLineBreak.docx")
        parser.findSectors()
        Assert.notEmpty(parser.nodes[0].content, "0 not found!")
        Assert.notEmpty(parser.nodes[1].content, "1 not found!")
    }

    @Test
    fun headerDetectedWithLineBreak() {
        val parser = base.createParser(directory, "headerWithLineBreak.docx")
        parser.findSectors()
        Assert.notEmpty(parser.nodes[0].content, "0 not found!")
        Assert.notEmpty(parser.nodes[1].content, "1 not found!")
    }
}