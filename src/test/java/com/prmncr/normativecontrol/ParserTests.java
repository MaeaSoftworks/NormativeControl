package com.prmncr.normativecontrol;

import com.prmncr.normativecontrol.components.CorrectDocumentParams;
import com.prmncr.normativecontrol.components.SectorKeywords;
import com.prmncr.normativecontrol.services.DocumentParser;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.ErrorType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
public class ParserTests {
    @MockBean
    private CorrectDocumentParams params;
    @Autowired
    private SectorKeywords keywords;

    private DocumentParser createParser(String filename) {
        try {
            return new DocumentParser(WordprocessingMLPackage.load(new FileInputStream("src/main/resources/test files/" + filename)), params, keywords);
        } catch (IOException | Docx4JException e) {
            System.out.println(e.getMessage());
            Assert.isTrue(false, "Parser cannot be initialized!");
            return new DocumentParser(null, params, keywords);
        }
    }

    @Test
    public void incorrectSizeTest() {
        var parser = createParser("incorrectWidth.docx");
        var result = new ArrayList<Error>();
        parser.checkPageSize(result);
        Assert.notEmpty(result, "Error not found!");
        Assert.state(result.get(0).errorType() == ErrorType.INCORRECT_PAGE_SIZE
                && result.get(0).paragraph() == -1
                && result.get(0).run() == -1, "Wrong error!");
    }

    @Test
    public void incorrectMarginTest() {
        var parser = createParser("incorrectWidth.docx");
        var result = new ArrayList<Error>();
        parser.checkPageMargins(result);
        Assert.notEmpty(result, "Error not found!");
        Assert.state(result.get(0).errorType() == ErrorType.INCORRECT_PAGE_MARGINS
                && result.get(0).paragraph() == -1
                && result.get(0).run() == -1, "Wrong error!");
    }

    @Test
    public void correctSectorsFound() {
        var parser = createParser("correctSectors.docx");
        var result = new ArrayList<Error>();
        parser.findSectors(result);
        Assert.notEmpty(parser.getSectors().get(0), "0 not found!");
        Assert.notEmpty(parser.getSectors().get(1), "1 not found!");
        Assert.notEmpty(parser.getSectors().get(2), "2 not found!");
        Assert.notEmpty(parser.getSectors().get(3), "3 not found!");
        Assert.notEmpty(parser.getSectors().get(4), "4 not found!");
        Assert.notEmpty(parser.getSectors().get(5), "5 not found!");
        Assert.notEmpty(parser.getSectors().get(6), "6 not found!");
        Assert.notEmpty(parser.getSectors().get(7), "7 not found!");
        Assert.isTrue(result.size() == 0, "There shouldn't be any errors!");
    }

    @Test
    public void incorrectSectorsFound() {
        var parser = createParser("skippedSector.docx");
        var result = new ArrayList<Error>();
        parser.findSectors(result);
        Assert.notEmpty(parser.getSectors().get(0), "0 must be found!");
        Assert.notEmpty(parser.getSectors().get(1), "1 must be found!");
        Assert.notEmpty(parser.getSectors().get(2), "2 must be found!");
        Assert.notEmpty(parser.getSectors().get(3), "3 must be found!");
        Assert.isTrue(parser.getSectors().get(4).size() == 0, "4 must be NOT found!");
        Assert.notEmpty(parser.getSectors().get(5), "5 must be found!");
        Assert.notEmpty(parser.getSectors().get(6), "6 must be found!");
        Assert.notEmpty(parser.getSectors().get(7), "7 must be found!");
        Assert.isTrue(result.size() == 1, "There should be error!");
    }

    @Test
    public void allSectorsSkipped() {
        var parser = createParser("skippedAllSectors.docx");
        var result = new ArrayList<Error>();
        parser.findSectors(result);
        Assert.notEmpty(parser.getSectors().get(0), "0 must be found!");
        Assert.isTrue(parser.getSectors().get(1).size() == 0, "1 must be NOT found!");
        Assert.isTrue(parser.getSectors().get(2).size() == 0, "2 must be NOT found!");
        Assert.isTrue(parser.getSectors().get(3).size() == 0, "3 must be NOT found!");
        Assert.isTrue(parser.getSectors().get(4).size() == 0, "4 must be NOT found!");
        Assert.isTrue(parser.getSectors().get(5).size() == 0, "5 must be NOT found!");
        Assert.isTrue(parser.getSectors().get(6).size() == 0, "6 must be NOT found!");
        Assert.notEmpty(parser.getSectors().get(7), "6 must be found!");
        Assert.isTrue(result.size() == 1, "There should be errors!");
    }

    @Test
    public void headerDetectedWithoutLineBreak() {
        var parser = createParser("headerWithoutLineBreak.docx");
        parser.findSectors(new ArrayList<>());
        Assert.notEmpty(parser.getSectors().get(0), "0 not found!");
        Assert.notEmpty(parser.getSectors().get(1), "1 not found!");
    }

    @Test
    public void headerDetectedWithLineBreak() {
        var parser = createParser("headerWithLineBreak.docx");
        parser.findSectors(new ArrayList<>());
        Assert.notEmpty(parser.getSectors().get(0), "0 not found!");
        Assert.notEmpty(parser.getSectors().get(1), "1 not found!");
    }
}
