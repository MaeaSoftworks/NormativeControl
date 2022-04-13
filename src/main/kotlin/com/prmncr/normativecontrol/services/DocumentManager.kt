package com.prmncr.normativecontrol.services

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.prmncr.normativecontrol.dbos.ProcessedDocument
import com.prmncr.normativecontrol.dtos.Document
import com.prmncr.normativecontrol.dtos.Error
import com.prmncr.normativecontrol.dtos.Result
import com.prmncr.normativecontrol.dtos.State
import com.prmncr.normativecontrol.events.NewDocumentEvent
import com.prmncr.normativecontrol.repositories.DocumentRepository
import com.prmncr.normativecontrol.serializers.ByteArraySerializer
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.util.*

@Component
class DocumentManager(
    private val queue: DocumentQueue,
    private val repository: DocumentRepository,
    private val publisher: ApplicationEventPublisher
) {
    fun addToQueue(file: ByteArray): String {
        val document = Document(UUID.randomUUID().toString(), file, result = null)
        queue.put(document)
        publisher.publishEvent(NewDocumentEvent(this, document.id))
        return document.id
    }

    fun getState(id: String): State? {
        return queue.getById(id)?.state
    }

    fun getResult(id: String): Result? {
        val document = queue.getById(id) ?: return null
        return document.result
    }

    fun getFile(id: String): Any? {
        val fileObject = repository.findById(id)
        return if (!fileObject.isPresent) {
            null
        } else object : Any() {
            @JsonSerialize(using = ByteArraySerializer::class)
            var file: ByteArray = fileObject.get().file
            val errors: List<Error> = fileObject.get().getDeserializedErrors()
        }
    }

    @Throws(JsonProcessingException::class)
    fun saveResult(id: String) {
        val document = queue.getById(id) ?: throw NullPointerException()
        val doc = ProcessedDocument(document.id, document.file, document.result?.errors)
        repository.save<ProcessedDocument>(doc)
        queue.remove(id)
    }

    fun dropDatabase() {
        repository.deleteAll()
    }

    fun delete(id: String?) {
        if (id != null) {
            queue.remove(id)
        }
    }
}