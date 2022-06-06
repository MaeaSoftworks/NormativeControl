package com.maeasoftworks.normativecontrol.documentation

import com.maeasoftworks.normativecontrol.controllers.DocumentController
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PossibleResponse
import com.maeasoftworks.normativecontrol.dto.Status
import com.maeasoftworks.normativecontrol.dto.response.FileResponse
import com.maeasoftworks.normativecontrol.dto.response.MistakesResponse
import com.maeasoftworks.normativecontrol.dto.response.StatusResponse
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus

sealed class DocumentDocs(d: DocumentManager) : DocumentController(d) {

    @Documentation("docs.method.getStatus.info")
    @PossibleResponse(HttpStatus.OK, Status::class, "docs.method.getStatus.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    abstract override fun getStatus(
        @Documentation("docs.method.common.args.id")
        documentId: String,
        @Documentation("docs.method.common.args.key")
        accessKey: String
    ): StatusResponse

    @Documentation("docs.method.getMistakes.info")
    @PossibleResponse(HttpStatus.OK, MistakesResponse::class, "docs.method.getMistakes.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    @PossibleResponse(HttpStatus.UNPROCESSABLE_ENTITY, description = "docs.method.common.response.422")
    abstract override fun getMistakes(
        @Documentation("docs.method.common.args.id")
        documentId: String,
        @Documentation("docs.method.common.args.key")
        accessKey: String
    ): MistakesResponse

    @Documentation("docs.method.getFile.info")
    @PossibleResponse(HttpStatus.OK, FileResponse::class, "docs.method.getFile.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    @PossibleResponse(HttpStatus.UNPROCESSABLE_ENTITY, description = "docs.method.common.response.422")
    abstract override fun getFile(
        @Documentation("docs.method.common.args.id")
        documentId: String,
        @Documentation("docs.method.common.args.key")
        accessKey: String
    ): FileResponse

    @Documentation("docs.method.getRawFile.info")
    @PossibleResponse(HttpStatus.OK, ByteArrayResource::class, "docs.method.getRawFile.response0")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.FORBIDDEN, description = "docs.method.common.response.403")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    @PossibleResponse(HttpStatus.UNPROCESSABLE_ENTITY, description = "docs.method.common.response.422")
    abstract override fun getRawFile(
        @Documentation("docs.method.common.args.id")
        documentId: String,
        @Documentation("docs.method.common.args.key")
        accessKey: String
    ): ByteArrayResource?
}