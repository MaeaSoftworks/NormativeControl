package com.prmncr.normativecontrol;

import com.prmncr.normativecontrol.dtos.ErrorType;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class HeaderTests {
    @Autowired
    TestBase base;

    @Test
    void headerIsCorrect() {
        val parser = base.createParser("correctHeaderStyle.docx");
        parser.findHeaderAllErrors(0);
        Assert.isTrue(parser.errors.size() == 0, "There shouldn't be any error!");
    }

    @Test
    void headerIsIncorrect() {
        val parser = base.createParser("wrongHeaderStyle.docx");
        parser.findHeaderAllErrors(0);
        Assert.isTrue(parser.errors.size() == 5, "There should be errors!");
        Assert.state(parser.errors.get(0).errorType() == ErrorType.INCORRECT_HEADER_ALIGNMENT
                && parser.errors.get(0).paragraph() == 0
                && parser.errors.get(0).run() == 0, "Wrong error!");
        Assert.state(parser.errors.get(1).errorType() == ErrorType.HEADER_IS_NOT_UPPERCASE
                && parser.errors.get(0).paragraph() == 0
                && parser.errors.get(0).run() == 0, "Wrong error!");
        Assert.state(parser.errors.get(2).errorType() == ErrorType.INCORRECT_TEXT_FONT
                && parser.errors.get(0).paragraph() == 0
                && parser.errors.get(0).run() == 0, "Wrong error!");
        Assert.state(parser.errors.get(3).errorType() == ErrorType.INCORRECT_TEXT_COLOR
                && parser.errors.get(0).paragraph() == 0
                && parser.errors.get(0).run() == 0, "Wrong error!");
        Assert.state(parser.errors.get(4).errorType() == ErrorType.INCORRECT_FONT_SIZE
                && parser.errors.get(0).paragraph() == 0
                && parser.errors.get(0).run() == 0, "Wrong error!");
    }
}
