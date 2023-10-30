package ru.maeasoftworks.normativecontrol.api.shared.services

import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class TokenGenerator {
    private val random = SecureRandom.getInstanceStrong()
    private val chars = ('0'..'9') + ('a'..'z') + ('A'..'Z')

    fun generateToken(length: Int): String {
        return (0..length).map { chars[random.nextInt(chars.size)] }.joinToString("")
    }
}