package com.maeasoftworks.server.dao

enum class MessageCode(code: Int) {
    INFO(10),
    WARN(20),
    ERROR(30),
    SUCCESS(50)
}