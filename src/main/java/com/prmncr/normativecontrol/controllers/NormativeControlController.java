package com.prmncr.normativecontrol.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class NormativeControlController {
    @GetMapping("/**")
    public String getMainPage() {
        return "main";
    }
}
