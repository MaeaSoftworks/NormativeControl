package com.prmncr.normativecontrol.factories;

import com.prmncr.normativecontrol.components.CorrectDocumentParams;
import com.prmncr.normativecontrol.components.SectorKeywords;
import com.prmncr.normativecontrol.services.DocumentParser;
import lombok.AllArgsConstructor;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class DocumentParserFactory {
    private CorrectDocumentParams params;
    private SectorKeywords keywords;

    public DocumentParser build(WordprocessingMLPackage docx) {
        return new DocumentParser(docx, params, keywords);
    }
}
