package ru.maeasoftworks.normativecontrol.api.domain

import ru.maeasoftworks.normativecontrol.core.abstractions.Profile

enum class EmailDomain(val value: String, val profile: Profile) {
    URFU_ME("urfu.me", Profile.UrFU),
    AT_URFU_RU("at.urfu.ru", Profile.UrFU);

    companion object {
        val allDomains = entries.map { it.value }
        val domainRegex = """^[\w\-.]+@([\w-]+\.+\w{2,4})$""".toRegex()

        fun ofEmail(email: String): EmailDomain? {
            return domainRegex
                .findAll(email)
                .map { domain -> EmailDomain.entries.firstOrNull { it.value == domain.value } }
                .firstOrNull()
        }
    }
}