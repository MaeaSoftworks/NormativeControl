package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.dtos.ProcessedDocument;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<ProcessedDocument, String> {
}
