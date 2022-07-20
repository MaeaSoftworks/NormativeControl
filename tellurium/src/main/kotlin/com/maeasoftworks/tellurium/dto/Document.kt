package com.maeasoftworks.tellurium.dto

import com.maeasoftworks.polonium.model.DocumentData

class Document(
    val id: String,
    val accessKey: String,
    var data: DocumentData,
    var password: String
)
