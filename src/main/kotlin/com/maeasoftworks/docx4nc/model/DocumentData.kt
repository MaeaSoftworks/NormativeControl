package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.Status

/**
 * Является классом информирующем о документе в целом
 */
class DocumentData(
    var file: ByteArray = ByteArray(0),

    /**
     * Переменная которая обозначает статус файла(изначально он имеет статус «в очереди»)
     */
    var status: Status = Status.QUEUE,

    /**
     * Переменная означающая ошибку, то есть неверный формат файла
     */
    var failureType: FailureType = FailureType.NONE
)
