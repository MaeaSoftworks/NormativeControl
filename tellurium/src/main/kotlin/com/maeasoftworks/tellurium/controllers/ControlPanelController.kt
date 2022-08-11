package com.maeasoftworks.tellurium.controllers

import com.maeasoftworks.tellurium.services.DocumentManager
import com.maeasoftworks.tellurium.utils.toResponse
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("control-panel")
class ControlPanelController(private val documentManager: DocumentManager) {

    @GetMapping("find-by-id")
    @ResponseBody
    fun findById(@RequestParam("document-id") documentId: String) =
        documentManager.find(documentId)

    @PostMapping("delete")
    @ResponseBody
    fun delete(@RequestParam("document-id") documentId: String) =
        documentManager.delete(documentId)

    @GetMapping("download/{document-id}")
    fun download(@PathVariable("document-id") documentId: String) =
        documentManager.getFile(documentId).toResponse(documentId)

    @GetMapping("render/{document-id}")
    fun getRender(@PathVariable("document-id") documentId: String) =
        documentManager.getRender(documentId)
}
