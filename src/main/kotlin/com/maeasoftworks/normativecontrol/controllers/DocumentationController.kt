package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.components.Documentation
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@CrossOrigin
@RequestMapping("docs")
class DocumentationController(private val documentation: Documentation) {
    @GetMapping
    fun mainPage(@RequestParam("section", required = false) section: String?, model: Model): String {
        model.addAttribute("methods", documentation.methods)
        model.addAttribute("entities", documentation.entities)
        if (section != null && "/" in section && documentation.methods.any { it.path == section }) {
            model.addAttribute("isMethod", true)
            model.addAttribute("currentMethod", documentation.methods.first { it.path == section })
        } else if (section in documentation.entities.map { it.name }) {
            model.addAttribute("isMethod", false)
            model.addAttribute("currentEntity", documentation.entities.first { it.name == section })
        }
        return "main"
    }
}
