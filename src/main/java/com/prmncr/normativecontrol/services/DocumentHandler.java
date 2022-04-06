package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.dtos.Document;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.ResultBody;
import com.prmncr.normativecontrol.dtos.State;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class DocumentHandler {
    public void handle(Document document) {
        XWPFDocument docx;
        try {
            docx = new XWPFDocument(new ByteArrayInputStream(document.getFile()));
        } catch (IOException e) {
            document.state = State.ERROR;
            document.result = new Result(true, e.getMessage());
            return;
        }
        document.result = new Result(new ResultBody(getMargins(docx)));
    }

    private String getMargins(XWPFDocument docx) {
        var margin = docx.getDocument().getBody().getSectPr().getPgMar();
        return margin.getTop().toString()
                + margin.getRight().toString()
                + margin.getBottom().toString()
                + margin.getLeft().toString();
    }
}
