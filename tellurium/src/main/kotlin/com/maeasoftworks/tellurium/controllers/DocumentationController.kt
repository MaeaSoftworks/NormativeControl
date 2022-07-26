package com.maeasoftworks.tellurium.controllers

import com.maeasoftworks.polonium.enums.MistakeType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("documentation")
class DocumentationController {
    @GetMapping("mistakes")
    @ResponseBody
    fun getMistakes(): Map<String, String> {
        return MistakeType.values().associate {it.name to it.ru}
    }
}