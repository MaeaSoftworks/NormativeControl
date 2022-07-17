package com.maeasoftworks.normativecontrol.dto.documentation

import com.maeasoftworks.docx4nc.enums.Status
import com.maeasoftworks.normativecontrol.controllers.DocumentController
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.PossibleResponse
import com.maeasoftworks.normativecontrol.dto.response.MistakesResponse
import com.maeasoftworks.normativecontrol.dto.response.StatusResponse
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

sealed class DocumentDocs(d: DocumentManager) : DocumentController(d) {

    @Documented("docs.method.getStatus.info")
    @PossibleResponse(HttpStatus.OK, Status::class, "docs.method.getStatus.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    abstract override fun getStatus(
        @Documented("docs.method.common.args.id")
        documentId: String,
        @Documented("docs.method.common.args.key")
        accessKey: String
    ): StatusResponse

    @Documented("docs.method.getMistakes.info")
    @PossibleResponse(HttpStatus.OK, MistakesResponse::class, "docs.method.getMistakes.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    @PossibleResponse(HttpStatus.UNPROCESSABLE_ENTITY, description = "docs.method.common.response.422")
    abstract override fun getMistakes(
        @Documented("docs.method.common.args.id")
        documentId: String,
        @Documented("docs.method.common.args.key")
        accessKey: String
    ): MistakesResponse

    @Documented("docs.method.getRawFile.info")
    @PossibleResponse(HttpStatus.OK, ByteArrayResource::class, "docs.method.getRawFile.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    abstract override fun getRawFile(
        @Documented("docs.method.common.args.id")
        documentId: String,
        @Documented("docs.method.common.args.key")
        accessKey: String
    ): ResponseEntity<ByteArrayResource?>

    @Documented("docs.method.getRender.info")
    @PossibleResponse(HttpStatus.OK, String::class, "docs.method.getRender.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    abstract override fun getRender(
        @Documented("docs.method.common.args.id")
        documentId: String,
        @Documented("docs.method.common.args.key")
        accessKey: String
    ): String?
}
