package com.maeasoftworks.normativecontrol

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NormativeControlApplication

fun main(args: Array<String>) {
    runApplication<NormativeControlApplication>(*args)
    (LoggerFactory.getLogger("org.docx4j") as Logger).level = Level.ERROR
}
