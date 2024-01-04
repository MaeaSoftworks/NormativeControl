package ru.maeasoftworks.normativecontrol.api.infrastructure.utils

import java.security.SecureRandom

object KeyGenerator {
    private val secureRandom = SecureRandom()
    private val alphabet = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val alphabetStrength = alphabet.size

    fun generate(len: Int = 32): String {
        return secureRandom.ints(len.toLong(), 0, alphabetStrength).boxed().map { alphabet[it] }.toList().joinToString("")
    }
}