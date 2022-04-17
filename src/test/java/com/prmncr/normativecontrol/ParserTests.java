package com.prmncr.normativecontrol;

import com.prmncr.normativecontrol.dtos.ErrorType;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class ParserTests extends TestsBase {
    @Test
    void incorrectSizeTest() {
        val parser = createParser("incorrectWidth.docx");
        parser.checkPageSize();
        Assert.notEmpty(parser.errors, "Error not found!");
        Assert.state(parser.errors.get(0).errorType() == ErrorType.INCORRECT_PAGE_SIZE
                && parser.errors.get(0).paragraph() == -1
                && parser.errors.get(0).run() == -1, "Wrong error!");
    }

    @Test
    void incorrectMarginTest() {
        val parser = createParser("incorrectMargin.docx");
        parser.checkPageMargins();
        Assert.notEmpty(parser.errors, "Error not found!");
        Assert.state(parser.errors.get(0).errorType() == ErrorType.INCORRECT_PAGE_MARGINS
                && parser.errors.get(0).paragraph() == -1
                && parser.errors.get(0).run() == -1, "Wrong error!");
    }

    @Test
    void correctSectorsFound() {
        val parser = createParser("correctSectors.docx");
        parser.findSectors();
        Assert.notEmpty(parser.sectors.get(0), "0 not found!");
        Assert.notEmpty(parser.sectors.get(1), "1 not found!");
        Assert.notEmpty(parser.sectors.get(2), "2 not found!");
        Assert.notEmpty(parser.sectors.get(3), "3 not found!");
        Assert.notEmpty(parser.sectors.get(4), "4 not found!");
        Assert.notEmpty(parser.sectors.get(5), "5 not found!");
        Assert.notEmpty(parser.sectors.get(6), "6 not found!");
        Assert.notEmpty(parser.sectors.get(7), "7 not found!");
        Assert.isTrue(parser.errors.size() == 0, "There shouldn't be any error!");
    }

    @Test
    void incorrectSectorsFound() {
        val parser = createParser("skippedSector.docx");
        parser.findSectors();
        Assert.notEmpty(parser.sectors.get(0), "0 must be found!");
        Assert.notEmpty(parser.sectors.get(1), "1 must be found!");
        Assert.notEmpty(parser.sectors.get(2), "2 must be found!");
        Assert.notEmpty(parser.sectors.get(3), "3 must be found!");
        Assert.isTrue(parser.sectors.get(4).size() == 0, "4 must be NOT found!");
        Assert.notEmpty(parser.sectors.get(5), "5 must be found!");
        Assert.notEmpty(parser.sectors.get(6), "6 must be found!");
        Assert.notEmpty(parser.sectors.get(7), "7 must be found!");
        Assert.isTrue(parser.errors.size() == 1, "There should be error!");
    }

    @Test
    void allSectorsSkipped() {
        val parser = createParser("skippedAllSectors.docx");
        parser.findSectors();
        Assert.notEmpty(parser.sectors.get(0), "0 must be found!");
        Assert.isTrue(parser.sectors.get(1).size() == 0, "1 must be NOT found!");
        Assert.isTrue(parser.sectors.get(2).size() == 0, "2 must be NOT found!");
        Assert.isTrue(parser.sectors.get(3).size() == 0, "3 must be NOT found!");
        Assert.isTrue(parser.sectors.get(4).size() == 0, "4 must be NOT found!");
        Assert.isTrue(parser.sectors.get(5).size() == 0, "5 must be NOT found!");
        Assert.isTrue(parser.sectors.get(6).size() == 0, "6 must be NOT found!");
        Assert.notEmpty(parser.sectors.get(7), "6 must be found!");
        Assert.isTrue(parser.errors.size() == 1, "There should be error!");
    }

    @Test
    void headerDetectedWithoutLineBreak() {
        val parser = createParser("headerWithoutLineBreak.docx");
        parser.findSectors();
        Assert.notEmpty(parser.sectors.get(0), "0 not found!");
        Assert.notEmpty(parser.sectors.get(1), "1 not found!");
    }

    @Test
    void headerDetectedWithLineBreak() {
        val parser = createParser("headerWithLineBreak.docx");
        parser.findSectors();
        Assert.notEmpty(parser.sectors.get(0), "0 not found!");
        Assert.notEmpty(parser.sectors.get(1), "1 not found!");
    }
}
