package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.dto.response.DocumentControlPanelResponse
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@CrossOrigin
@RestController
@RequestMapping("control-panel")
@ConditionalOnExpression("\${controllers.api}")
class ControlPanelController(private val documentManager: DocumentManager) {
    @GetMapping("find-by-id")
    @ResponseBody
    fun findById(@RequestParam("document-id") documentId: String): DocumentControlPanelResponse {
        return documentManager.find(documentId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @GetMapping("delete")
    @ResponseBody
    fun delete(@RequestParam("document-id") documentId: String) {
        documentManager.deleteById(documentId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
