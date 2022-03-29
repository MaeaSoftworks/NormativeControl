package com.prmncr.normativecontrol.listeners;

import com.prmncr.normativecontrol.services.DocumentStorage;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class NewDocumentListener {
	private final DocumentStorage documentStorage;

	public NewDocumentListener(DocumentStorage documentStorage) {
	    this.documentStorage = documentStorage;
	}

	@Async
	@EventListener
	public void handleDocument(NewDocumentEvent event) {
		var document = documentStorage.getById(event.getDocumentId());
		document.setState(State.PROCESSING);
		XWPFDocument docx;
		try {
			docx = new XWPFDocument(Files.newInputStream(document.getPath()));
		} catch (IOException e) {
			document.setState(State.ERROR);
			document.setResult(new Result(true, e.getMessage()));
			return;
		}
		document.setResult(new Result(docx.getDocument().getBody().getSectPr().getPgMar().xmlText()));
		document.setState(State.READY);
	}
}
