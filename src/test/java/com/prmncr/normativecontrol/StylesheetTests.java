package com.prmncr.normativecontrol;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class StylesheetTests extends TestsBase {
    @Test
    void headerIsCorrect() {
        val parser = createParser("overwrittenDefaultStyle.docx");
        parser.findHeaderAllErrors(0);
        Assert.isTrue(parser.errors.size() == 0, "There shouldn't be any error!");
    }

    @Test
    void foundAllErrors() {
        val parser = createParser("veryWrongText.docx");
        parser.findGeneralAllErrors(0);
        Assert.isTrue(parser.errors.size() > 0, "There should be errors!");
    }
}
