package com.prmncr.normativecontrol

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class NormativeControlApplication

fun main(args: Array<String>) {
    runApplication<NormativeControlApplication>(*args)
}