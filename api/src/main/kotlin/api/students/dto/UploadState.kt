package api.students.dto

import software.amazon.awssdk.services.s3.model.CompletedPart

class UploadState(val bucket: String, val objectName: String) {
    var uploadId: String? = null
    var partCounter = 0
    var completedParts: MutableMap<Int, CompletedPart> = HashMap()
    var buffered = 0
}