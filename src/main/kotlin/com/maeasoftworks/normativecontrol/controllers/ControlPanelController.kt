package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.services.DocumentManager
import com.maeasoftworks.normativecontrol.utils.toResponse
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("control-panel")
class ControlPanelController(private val documentManager: DocumentManager) {

    @GetMapping("find-by-id")
    @ResponseBody
    fun findById(@RequestParam("document-id") documentId: String) = documentManager.find(documentId)

    @PostMapping("delete")
    @ResponseBody
    fun delete(@RequestParam("document-id") documentId: String) = documentManager.delete(documentId)

    @GetMapping("download/{document-id}")
    fun download(@PathVariable("document-id") id: String) = documentManager.getFile(id).toResponse(id)

    @GetMapping("render/{document-id}")
    fun getRender(@PathVariable("document-id") id: String) = documentManager.getRender(id)
}
