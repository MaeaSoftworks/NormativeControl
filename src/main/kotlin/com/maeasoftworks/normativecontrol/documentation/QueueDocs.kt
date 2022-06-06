package com.maeasoftworks.normativecontrol.documentation

import com.maeasoftworks.normativecontrol.controllers.QueueController
import com.maeasoftworks.normativecontrol.documentation.annotations.BodyParam
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PossibleResponse
import com.maeasoftworks.normativecontrol.dto.response.QueueResponse
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile

sealed class QueueDocs(d: DocumentManager) : QueueController(d) {
    @Documentation("docs.method.reserve.info")
    @PossibleResponse(HttpStatus.OK, QueueResponse::class, "docs.method.reserve.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    abstract override fun reserve(
        @Documentation("docs.method.common.args.key")
        accessKey: String
    ): QueueResponse

    @Documentation("docs.method.enqueue.info")
    @PossibleResponse(HttpStatus.ACCEPTED, description = "docs.method.enqueue.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    @PossibleResponse(HttpStatus.UNPROCESSABLE_ENTITY, description = "docs.method.common.response.422")
    abstract override fun enqueue(
        @Documentation("docs.method.common.args.id")
        documentId: String,
        @Documentation("docs.method.common.args.key")
        accessKey: String,
        @Documentation("docs.method.enqueue.arg0")
        @BodyParam
        file: MultipartFile
    )
}