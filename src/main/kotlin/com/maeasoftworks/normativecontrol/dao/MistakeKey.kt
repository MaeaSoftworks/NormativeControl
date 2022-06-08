package com.maeasoftworks.normativecontrol.dao

import javax.persistence.Column

data class MistakeKey(
    @Column(name = "document_id")
    val documentId: String = "",
    @Column(name = "mistake_id")
    val mistakeId: Long = 0
) : java.io.Serializable
