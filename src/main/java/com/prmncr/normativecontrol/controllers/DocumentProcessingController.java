package com.prmncr.normativecontrol.controllers;

import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.services.DocumentManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("api")
public class DocumentProcessingController {
	private final DocumentManager documentsManager;
	@Value("${files.directory}")
	private String directory;

	public DocumentProcessingController(DocumentManager documentsManager) {
	    this.documentsManager = documentsManager;
	}

	@GetMapping("/")
	public String listMappings() {
		return "OK";
	}

	@GetMapping("get-status")
	@ResponseBody
	public ResponseEntity<State> getStatus(@RequestParam(value = "id") String id) {
		return new ResponseEntity<>(documentsManager.getStatus(id), HttpStatus.OK);
	}

	@GetMapping("get-result")
	@ResponseBody
	public ResponseEntity<String> getResult(@RequestParam(value = "id") String id) {
		return new ResponseEntity<>(documentsManager.getResult(id), HttpStatus.OK);
	}

	@PostMapping("upload-document")
	public ResponseEntity<String> getFile(@RequestParam("file") MultipartFile file) throws IOException {
		if (!Files.exists(Path.of(directory))){
			Files.createDirectories(Path.of(directory));
		}
		var tempFile = File.createTempFile(UUID.randomUUID().toString(), ".docx", new File(this.directory));
		file.transferTo(tempFile);
		var id = documentsManager.addToQueue(tempFile.toPath());
		return new ResponseEntity<>(id, HttpStatus.ACCEPTED);
	}
}
