package ru.maeasoftworks.normativecontrol.api.domain

import ru.maeasoftworks.normativecontrol.core.abstractions.Profile

enum class Organization(val domains: Set<String>, val profile: Profile) {
    UrFU(setOf("urfu.me", "at.urfu.ru"), Profile.UrFU);

    companion object {
        val allDomains = entries.flatMap { it.domains }
        val domainRegex = """^[\w\-.]+@([\w-]+\.+\w{2,4})$""".toRegex()

        fun getByEmail(email: String): Organization? {
            return domainRegex
                .findAll(email.lowercase())
                .map { domain -> Organization.entries.firstOrNull { domain.groups.last()?.value in it.domains } }
                .firstOrNull()
        }
    }
}