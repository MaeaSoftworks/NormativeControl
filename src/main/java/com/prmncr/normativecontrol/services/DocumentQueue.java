package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.dtos.Document;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentQueue {
    private final Map<String, Document> documentMap = new ConcurrentHashMap<>();

    public void put(Document document) {
        documentMap.put(document.getId(), document);
    }

    @Nullable
    public Document getById(String id) {
        try {
            return documentMap.get(id);
        } catch (NullPointerException e) {
            return null;
        }

    }

    public void remove(String id) {
        documentMap.remove(id);
    }
}
