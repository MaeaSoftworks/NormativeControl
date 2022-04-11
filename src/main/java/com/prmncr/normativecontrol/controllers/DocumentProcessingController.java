package com.prmncr.normativecontrol.controllers;

import com.prmncr.normativecontrol.dbos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.services.DocumentManager;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("api")
@AllArgsConstructor
public class DocumentProcessingController {
    private final DocumentManager documentsManager;

    @GetMapping("get-status")
    @ResponseBody
    public ResponseEntity<State> getStatus(@RequestParam(value = "id") String id) {
        return new ResponseEntity<>(documentsManager.getStatus(id), HttpStatus.OK);
    }

    @GetMapping("get-result")
    @ResponseBody
    public ResponseEntity<Result> getResult(@RequestParam(value = "id") String id) {
        return new ResponseEntity<>(documentsManager.getResult(id), HttpStatus.OK);
    }

    @PostMapping("upload-document")
    @ResponseBody
    public ResponseEntity<Object> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        val documentId = documentsManager.addToQueue(file.getBytes());
        return new ResponseEntity<>(new Object() {
            @SuppressWarnings("unused")
            public final String id = documentId;
        }, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "load-result")
    @ResponseBody
    public ResponseEntity<ProcessedDocument> getDocument(@RequestParam(value = "id") String id) {
        return new ResponseEntity<>(documentsManager.getFile(id), HttpStatus.OK);
    }

    @GetMapping("drop-db")
    @ResponseBody
    public ResponseEntity<String> dropDatabase() {
        documentsManager.dropDatabase();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
