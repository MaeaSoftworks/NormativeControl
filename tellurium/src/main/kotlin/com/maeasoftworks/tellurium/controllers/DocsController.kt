package com.maeasoftworks.tellurium.controllers

import com.maeasoftworks.tellurium.documentation.DocumentationCreator
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@CrossOrigin
@RequestMapping("docs")
class DocsController(private val documentationCreator: DocumentationCreator) {
    @GetMapping
    fun mainPage(@RequestParam("section", required = false) section: String?, model: Model): String {
        model.addAttribute("methods", documentationCreator.controllers)
        model.addAttribute("entities", documentationCreator.entities)
        if (section != null && "/" in section && documentationCreator.controllers.any { it.path == section }) {
            model.addAttribute("isMethod", true)
            model.addAttribute("currentMethod", documentationCreator.controllers.first { it.path == section })
        } else if (section in documentationCreator.entities.map { it.name }) {
            model.addAttribute("isMethod", false)
            model.addAttribute("currentEntity", documentationCreator.entities.first { it.name == section })
        }
        return "main"
    }
}
