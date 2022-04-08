package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.components.CorrectDocumentParams;
import com.prmncr.normativecontrol.components.SectorKeywords;
import com.prmncr.normativecontrol.dtos.*;
import com.prmncr.normativecontrol.dtos.Error;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
        XWPFDocument docx;
        try {
            docx = new XWPFDocument(new ByteArrayInputStream(document.getFile()));
        } catch (IOException e) {
            document.setState(State.ERROR);
            document.setResult(new Result(FailureType.FILE_READING_ERROR));
            return;
        }
        document.setResult(new Result(getResult(docx)));
    }

    private List<Error> getResult(XWPFDocument docx) {
        DocumentParser parser = new DocumentParser(docx, params, keywords);
        return parser.runStyleCheck();
    }
}
