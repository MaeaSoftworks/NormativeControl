package com.prmncr.normativecontrol;

import com.prmncr.normativecontrol.components.CorrectDocumentParams;
import com.prmncr.normativecontrol.services.DocxParser;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.ErrorType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
public class ParserTests {
    @MockBean
    CorrectDocumentParams params;

    @Test
    public void incorrectSizeTest() {
        var parser = new DocxParser(params);
        try {
            parser.init(new XWPFDocument(new FileInputStream("src/main/resources/test files/incorrectWidth.docx")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Assert.isTrue(false, "Parser cannot initialize!");
        }
        var result = new ArrayList<Error>();
        ReflectionTestUtils.invokeMethod(parser, "checkPageSize", result);
        Assert.notEmpty(result, "Error not found!");
        Assert.state(result.get(0).getErrorType() == ErrorType.INCORRECT_PAGE_SIZE
                && result.get(0).getParagraph() == -1
                && result.get(0).getRun() == -1, "Wrong error!");
    }

    @Test
    public void incorrectMarginTest() {
        var parser = new DocxParser(params);
        try {
            parser.init(new XWPFDocument(new FileInputStream("src/main/resources/test files/incorrectWidth.docx")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Assert.isTrue(false, "Parser cannot initialize!");
        }
        var result = new ArrayList<Error>();
        ReflectionTestUtils.invokeMethod(parser, "checkPageMargins", result);
        Assert.notEmpty(result, "Error not found!");
        Assert.state(result.get(0).getErrorType() == ErrorType.INCORRECT_PAGE_MARGINS
                && result.get(0).getParagraph() == -1
                && result.get(0).getRun() == -1, "Wrong error!");
    }
}
