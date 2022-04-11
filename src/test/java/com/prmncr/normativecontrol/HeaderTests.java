package com.prmncr.normativecontrol;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class HeaderTests extends TestSetup {
    @Test
    void headerIsCorrect() {
        val parser = createParser("correctHeaderStyle.docx");
        parser.checkHeaderStyle(0);
        Assert.isTrue(parser.getErrors().size() == 0, "There shouldn't be any error!");
    }
}
