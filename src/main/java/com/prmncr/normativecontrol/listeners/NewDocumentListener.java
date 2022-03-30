package com.prmncr.normativecontrol.listeners;

import com.prmncr.normativecontrol.dtos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.ResultBody;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.dtos.documentparams.PageMargin;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.services.DocumentRepository;
import com.prmncr.normativecontrol.services.DocumentStorage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class NewDocumentListener {
	private final DocumentRepository documentRepository;
	private final DocumentStorage documentStorage;

	public NewDocumentListener(DocumentRepository documentRepository, DocumentStorage documentStorage) {
		this.documentRepository = documentRepository;
		this.documentStorage = documentStorage;
	}

	@Async
	@EventListener
	public void handleDocument(NewDocumentEvent event) throws InterruptedException {
		var document = documentStorage.getById(event.getDocumentId());
		document.state = State.PROCESSING;
		Thread.sleep(5000);
		XWPFDocument docx;
		try {
			docx = new XWPFDocument(new ByteArrayInputStream(document.getFile()));
		} catch (IOException e) {
			document.state = State.ERROR;
			document.result = new Result(true, e.getMessage());
			return;
		}
		var margin = docx.getDocument().getBody().getSectPr().getPgMar();
		document.result = new Result(new ResultBody(new PageMargin(
				Integer.parseInt(margin.getTop().toString()),
				Integer.parseInt(margin.getRight().toString()),
				Integer.parseInt(margin.getBottom().toString()),
				Integer.parseInt(margin.getLeft().toString()))));
		document.state = State.READY;
		documentRepository.save(new ProcessedDocument(document.getId(), document.getFile()));
	}
}
