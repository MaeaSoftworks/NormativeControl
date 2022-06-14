package com.maeasoftworks.normativecontrol.dto.documentation

import com.maeasoftworks.normativecontrol.controllers.QueueController
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.BodyParam
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.PossibleResponse
import com.maeasoftworks.normativecontrol.dto.response.QueueResponse
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile

sealed class QueueDocs(d: DocumentManager) : QueueController(d) {
    @Documented("docs.method.reserve.info")
    @PossibleResponse(HttpStatus.OK, QueueResponse::class, "docs.method.reserve.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    abstract override fun reserve(
        @Documented("docs.method.common.args.key")
        accessKey: String
    ): QueueResponse

    @Documented("docs.method.enqueue.info")
    @PossibleResponse(HttpStatus.ACCEPTED, description = "docs.method.enqueue.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    @PossibleResponse(HttpStatus.UNPROCESSABLE_ENTITY, description = "docs.method.common.response.422")
    abstract override fun enqueue(
        @Documented("docs.method.common.args.id")
        documentId: String,
        @Documented("docs.method.common.args.key")
        accessKey: String,
        @Documented("docs.method.enqueue.arg0")
        @BodyParam
        file: MultipartFile
    )
}
