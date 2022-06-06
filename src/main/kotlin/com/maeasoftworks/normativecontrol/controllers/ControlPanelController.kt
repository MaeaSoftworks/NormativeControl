package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
@RequestMapping("control-panel")
@ConditionalOnExpression("\${controllers.api}")
class ControlPanelController(private val documentManager: DocumentManager) {
    @GetMapping
    fun helloWorld(): String {
        return "Hello ${SecurityContextHolder.getContext().authentication.name}!"
    }
}