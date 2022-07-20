package com.maeasoftworks.tellurium

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NormativeControlApplication {
    // for heroku
    fun main(args: Array<String>) = com.maeasoftworks.tellurium.main(args)
}

fun main(args: Array<String>) {
    runApplication<NormativeControlApplication>(*args)
}
