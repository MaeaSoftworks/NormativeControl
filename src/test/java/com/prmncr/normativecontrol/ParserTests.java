package com.prmncr.normativecontrol;

import com.prmncr.normativecontrol.components.CorrectDocumentParams;
import com.prmncr.normativecontrol.components.SectorKeywords;
import com.prmncr.normativecontrol.services.DocumentParser;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.ErrorType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
public class ParserTests {
    @MockBean
    private CorrectDocumentParams params;
    @Autowired
    private SectorKeywords keywords;

    @Test
    public void incorrectSizeTest() {
        try {
            var parser = new DocumentParser(new XWPFDocument(new FileInputStream("src/main/resources/test files/incorrectWidth.docx")), params, keywords);
            var result = new ArrayList<Error>();
            ReflectionTestUtils.invokeMethod(parser, "checkPageSize", result);
            Assert.notEmpty(result, "Error not found!");
            Assert.state(result.get(0).errorType() == ErrorType.INCORRECT_PAGE_SIZE
                    && result.get(0).paragraph() == -1
                    && result.get(0).run() == -1, "Wrong error!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Assert.isTrue(false, "Parser cannot be initialized!");
        }
    }

    @Test
    public void incorrectMarginTest() {
        try {
            var parser = new DocumentParser(new XWPFDocument(new FileInputStream("src/main/resources/test files/incorrectWidth.docx")), params, keywords);
            var result = new ArrayList<Error>();
            ReflectionTestUtils.invokeMethod(parser, "checkPageMargins", result);
            Assert.notEmpty(result, "Error not found!");
            Assert.state(result.get(0).errorType() == ErrorType.INCORRECT_PAGE_MARGINS
                    && result.get(0).paragraph() == -1
                    && result.get(0).run() == -1, "Wrong error!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Assert.isTrue(false, "Parser cannot be initialized!");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void correctSectorsFound() {
        try {
            var parser = new DocumentParser(new XWPFDocument(new FileInputStream("src/main/resources/test files/correctSectors.docx")), params, keywords);
            var result = new ArrayList<Error>();
            ReflectionTestUtils.invokeMethod(parser, "findSectors", result);
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "frontPage"), "1 not found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "contents"), "2 not found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "introduction"), "3 not found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "essay"), "4 not found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "conclusion"), "5 not found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "references"), "6 not found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "appendix"), "7 not found!");
            Assert.isTrue(result.size() == 0, "There shouldn't be any errors!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Assert.isTrue(false, "Parser cannot be initialized!");
        } catch (ClassCastException ex) {
            Assert.isTrue(false, "Wrong field!");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void incorrectSectorsFound() {
        try {
            var parser = new DocumentParser(new XWPFDocument(new FileInputStream("src/main/resources/test files/skippedSector.docx")), params, keywords);
            var result = new ArrayList<Error>();
            ReflectionTestUtils.invokeMethod(parser, "findSectors", result);
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "frontPage"), "1 MUST BE found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "contents"), "2 MUST BE found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "introduction"), "3 MUST BE found!");
            Assert.isTrue(((List<Object>) Objects.requireNonNull(ReflectionTestUtils.getField(parser, "essay"))).size() == 0, "4 MUST BE not found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "conclusion"), "5 MUST BE found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "references"), "6 MUST BE found!");
            Assert.notEmpty((List<Object>) ReflectionTestUtils.getField(parser, "appendix"), "7 MUST BE found!");
            Assert.isTrue(result.size() == 1, "There should be error!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Assert.isTrue(false, "Parser cannot be initialized!");
        } catch (ClassCastException ex) {
            Assert.isTrue(false, "Wrong field!");
        }
    }
}
