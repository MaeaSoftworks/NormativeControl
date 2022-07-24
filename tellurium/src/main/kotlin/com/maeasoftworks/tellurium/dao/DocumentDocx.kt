package com.maeasoftworks.tellurium.dao

import javax.persistence.Lob

interface DocumentDocx {
    val documentId: String
    val bytes: ByteArray
}