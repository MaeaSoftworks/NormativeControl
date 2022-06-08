package com.maeasoftworks.normativecontrol.dto

import com.maeasoftworks.docx4nc.model.DocumentData

class Document(
    val id: String,
    val accessKey: String,
    var data: DocumentData,
    var password: String
)
