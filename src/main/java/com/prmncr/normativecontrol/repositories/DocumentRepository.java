package com.prmncr.normativecontrol.repositories;

import com.prmncr.normativecontrol.dbos.ProcessedDocument;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<ProcessedDocument, String> {}
