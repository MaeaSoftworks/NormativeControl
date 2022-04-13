package com.prmncr.normativecontrol

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
internal class SectorsTests : TestsBase() {
    @Test
    fun correctSectorsFound() {
        val parser = createParser("sectors/correctSectors.docx")
        parser.findSectors()
        Assert.notEmpty(parser.sectors[0], "0 not found!")
        Assert.notEmpty(parser.sectors[1], "1 not found!")
        Assert.notEmpty(parser.sectors[2], "2 not found!")
        Assert.notEmpty(parser.sectors[3], "3 not found!")
        Assert.notEmpty(parser.sectors[4], "4 not found!")
        Assert.notEmpty(parser.sectors[5], "5 not found!")
        Assert.notEmpty(parser.sectors[6], "6 not found!")
        Assert.notEmpty(parser.sectors[7], "7 not found!")
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun incorrectSectorsFound() {
        val parser = createParser("sectors/skippedSector.docx")
        parser.findSectors()
        Assert.notEmpty(parser.sectors[0], "0 must be found!")
        Assert.notEmpty(parser.sectors[1], "1 must be found!")
        Assert.notEmpty(parser.sectors[2], "2 must be found!")
        Assert.notEmpty(parser.sectors[3], "3 must be found!")
        Assert.isTrue(parser.sectors[4].size == 0, "4 must be NOT found!")
        Assert.notEmpty(parser.sectors[5], "5 must be found!")
        Assert.notEmpty(parser.sectors[6], "6 must be found!")
        Assert.notEmpty(parser.sectors[7], "7 must be found!")
        Assert.isTrue(parser.errors.size == 1, "There should be error!")
    }

    @Test
    fun allSectorsSkipped() {
        val parser = createParser("sectors/skippedAllSectors.docx")
        parser.findSectors()
        Assert.notEmpty(parser.sectors[0], "0 must be found!")
        Assert.isTrue(parser.sectors[1].size == 0, "1 must be NOT found!")
        Assert.isTrue(parser.sectors[2].size == 0, "2 must be NOT found!")
        Assert.isTrue(parser.sectors[3].size == 0, "3 must be NOT found!")
        Assert.isTrue(parser.sectors[4].size == 0, "4 must be NOT found!")
        Assert.isTrue(parser.sectors[5].size == 0, "5 must be NOT found!")
        Assert.isTrue(parser.sectors[6].size == 0, "6 must be NOT found!")
        Assert.notEmpty(parser.sectors[7], "6 must be found!")
        Assert.isTrue(parser.errors.size == 1, "There should be error!")
    }
}