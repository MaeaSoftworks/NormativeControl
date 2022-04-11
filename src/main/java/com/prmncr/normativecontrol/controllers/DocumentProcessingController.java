package com.prmncr.normativecontrol.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
@RequestMapping("api/documents")
@AllArgsConstructor
public class DocumentProcessingController {
    private final DocumentManager documentsManager;

    @GetMapping("state")
    @ResponseBody
    public ResponseEntity<Object> getState(@RequestParam(value = "id") String id) {
        val s = documentsManager.getState(id);
        return s != null ? new ResponseEntity<>(new Object() {public final State state = s;}, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("result")
    @ResponseBody
    public ResponseEntity<Object> getResult(@RequestParam(value = "id") String id) {
        val r = documentsManager.getResult(id);
        return r != null ? new ResponseEntity<>(new Object() {public final Result result = r;}, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("upload")
    @ResponseBody
    public ResponseEntity<Object> uploadDocument(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        val filename = file.getOriginalFilename();
        if (filename == null || filename.equals("")) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        val extension = filename.split("\\.");
        if (!extension[1].equals("docx")) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        val documentId = documentsManager.addToQueue(bytes);
        return new ResponseEntity<>(new Object() {public final String id = documentId;}, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "result/{id}")
    @ResponseBody
    public ResponseEntity<Object> loadResult(@PathVariable(value = "id") String id) {
        Object file = null;
        try {
            file = documentsManager.getFile(id);
        } catch (JsonProcessingException e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return file != null ? new ResponseEntity<>(file, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping(value = "result")
    @ResponseBody
    public ResponseEntity<String> saveResult(@RequestParam(value = "id") String id) {
        try {
            documentsManager.saveResult(id);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("result")
    @ResponseBody
    public ResponseEntity<String> deleteResult(@RequestParam(value = "id") String id) {
        documentsManager.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("drop-database")
    @ResponseBody
    public ResponseEntity<String> dropDatabase() {
        documentsManager.dropDatabase();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
