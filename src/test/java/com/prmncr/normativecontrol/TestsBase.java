package com.prmncr.normativecontrol;

import com.prmncr.normativecontrol.components.DocumentParserBuilder;
import com.prmncr.normativecontrol.services.DocumentParser;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.util.Assert;

import java.io.FileInputStream;
import java.io.IOException;

@TestConfiguration
public class TestsBase {
    @Autowired
    protected DocumentParserBuilder factory;

    protected DocumentParser createParser(String filename) {
        try {
            return factory.build(WordprocessingMLPackage.load(new FileInputStream("src/main/resources/test files/" + filename)));
        } catch (IOException | Docx4JException | IllegalAccessException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Parser cannot be initialized!");
        }
    }
}
