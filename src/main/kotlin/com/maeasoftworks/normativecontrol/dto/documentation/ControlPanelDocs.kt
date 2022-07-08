package com.maeasoftworks.normativecontrol.dto.documentation

import com.maeasoftworks.normativecontrol.controllers.ControlPanelController
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.PossibleResponse
import com.maeasoftworks.normativecontrol.dto.response.DocumentControlPanelResponse
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

sealed class ControlPanelDocs(d: DocumentManager): ControlPanelController(d) {
    @Documented("docs.method.admin.findById.info")
    @PossibleResponse(HttpStatus.OK, DocumentControlPanelResponse::class, description = "docs.method.admin.findById.response0")
    @PossibleResponse(HttpStatus.UNAUTHORIZED, description = "docs.method.common.response.401")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    abstract override fun findById(
        @Documented("docs.method.common.args.id")
        documentId: String
    ): DocumentControlPanelResponse

    @Documented("docs.method.admin.download.info")
    @PossibleResponse(HttpStatus.OK, ByteArrayResource::class, "docs.method.getRawFile.response0")
    @PossibleResponse(HttpStatus.UNAUTHORIZED, description = "docs.method.common.response.401")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    abstract override fun download(
        @Documented("docs.method.common.args.id")
        id: String
    ): ResponseEntity<ByteArrayResource?>

    @Documented("docs.method.admin.getRender.info")
    @PossibleResponse(HttpStatus.OK, String::class, "docs.method.getRender.response0")
    @PossibleResponse(HttpStatus.UNAUTHORIZED, description = "docs.method.common.response.401")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    abstract override fun getRender(
        @Documented("docs.method.common.args.id")
        id: String
    ): String?

    @Documented("docs.method.admin.delete.info")
    @PossibleResponse(HttpStatus.OK, description = "docs.method.admin.delete.response0")
    @PossibleResponse(HttpStatus.UNAUTHORIZED, description = "docs.method.common.response.401")
    @PossibleResponse(HttpStatus.NOT_FOUND, description = "docs.method.common.response.404")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.400")
    abstract override fun delete(
        @Documented("docs.method.common.args.id")
        documentId: String
    )
}
