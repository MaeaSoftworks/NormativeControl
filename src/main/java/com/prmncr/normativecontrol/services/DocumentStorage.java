package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.dtos.Document;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentStorage {
	private final Map<String, Document> documentMap = new ConcurrentHashMap<>();

	public void put(Document document) {
		documentMap.put(document.getId(), document);
	}

	public Document getById(String id) {
		return documentMap.get(id);
	}

	public void remove(String id) throws IOException {
		Files.delete(documentMap.get(id).getPath());
		documentMap.remove(id);
	}
}
