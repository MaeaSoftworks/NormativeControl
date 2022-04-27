package com.prmncr.normativecontrol.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.services.DocumentManager;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("documents")
@AllArgsConstructor
public class DocumentProcessingController {
    private final DocumentManager documentsManager;

    @GetMapping("state")
    public Map<String, State> getState(@RequestParam(value = "id") String id) {
        val state = documentsManager.getState(id);
        if (state == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }
        return Collections.singletonMap("state", state);
    }

    @PostMapping("upload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> uploadDocument(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required arguments were empty");
        }
        val filename = file.getOriginalFilename();
        if (filename == null || filename.equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required arguments were empty");
        }
        val extension = filename.split("\\.");
        if (!extension[1].equals("docx")) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Can not process this document");
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Can not process this document");
        }
        return Collections.singletonMap("id", documentsManager.addToQueue(bytes));
    }

    @GetMapping("errors")
    public Map<String, List<Error>> getErrors(@RequestParam(value = "id") String id) throws JsonProcessingException {
        val data = documentsManager.getData(id);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }
        return Collections.singletonMap("errors", data.getDeserializedErrors());
    }

    @GetMapping("file")
    public Resource getFile(@RequestParam(value = "id") String id) {
        val file = documentsManager.getFile(id);
        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }
        return new ByteArrayResource(file.getFile());
    }

    @GetMapping("drop-database")
    @ResponseStatus(HttpStatus.OK)
    public void dropDatabase() {
        documentsManager.dropDatabase();
    }
}
