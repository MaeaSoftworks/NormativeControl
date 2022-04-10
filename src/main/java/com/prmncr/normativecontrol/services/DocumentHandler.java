package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.components.CorrectDocumentParams;
import com.prmncr.normativecontrol.components.SectorKeywords;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.*;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
@SuppressWarnings("ClassCanBeRecord")
public class DocumentHandler {
    private final CorrectDocumentParams params;
    private final SectorKeywords keywords;

    public DocumentHandler(CorrectDocumentParams params, SectorKeywords keywords) {
        this.params = params;
        this.keywords = keywords;
    }

    public void handle(Document document) {
        WordprocessingMLPackage docx;
        try {
            docx = WordprocessingMLPackage.load(new ByteArrayInputStream(document.getFile()));
        } catch (Docx4JException e) {
            document.setState(State.ERROR);
            document.setResult(new Result(FailureType.FILE_READING_ERROR));
            return;
        }
        document.setResult(new Result(getResult(docx)));
    }

    private List<Error> getResult(WordprocessingMLPackage docx) {
        DocumentParser parser = new DocumentParser(docx, params, keywords);
        return parser.runStyleCheck();
    }
}
