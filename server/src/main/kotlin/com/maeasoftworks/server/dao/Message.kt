package com.maeasoftworks.server.dao

data class Message(
    val id: String,
    val code: MessageCode,
    val message: String
)