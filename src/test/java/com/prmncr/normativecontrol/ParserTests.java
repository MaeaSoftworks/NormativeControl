package com.prmncr.normativecontrol;

import com.prmncr.normativecontrol.components.CorrectDocumentParams;
import com.prmncr.normativecontrol.services.DocumentParser;
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
        try {
            var parser = new DocumentParser(new XWPFDocument(new FileInputStream("src/main/resources/test files/incorrectWidth.docx")), params);
            var result = new ArrayList<Error>();
            ReflectionTestUtils.invokeMethod(parser, "checkPageSize", result);
            Assert.notEmpty(result, "Error not found!");
            Assert.state(result.get(0).errorType() == ErrorType.INCORRECT_PAGE_SIZE
                    && result.get(0).paragraph() == -1
                    && result.get(0).run() == -1, "Wrong error!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Assert.isTrue(false, "Parser cannot initialize!");
        }
    }

    @Test
    public void incorrectMarginTest() {
        try {
            var parser = new DocumentParser(new XWPFDocument(new FileInputStream("src/main/resources/test files/incorrectWidth.docx")), params);
            var result = new ArrayList<Error>();
            ReflectionTestUtils.invokeMethod(parser, "checkPageMargins", result);
            Assert.notEmpty(result, "Error not found!");
            Assert.state(result.get(0).errorType() == ErrorType.INCORRECT_PAGE_MARGINS
                    && result.get(0).paragraph() == -1
                    && result.get(0).run() == -1, "Wrong error!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Assert.isTrue(false, "Parser cannot initialize!");
        }
    }
}
