package com.maeasoftworks.normativecontrol

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NormativeControlApplication

fun main(args: Array<String>) {
    runApplication<NormativeControlApplication>(*args)
    // (LoggerFactory.getLogger("org.docx4j") as Logger).level = Level.ERROR
}
