package com.maeasoftworks.normativecontrol.entities

import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import java.time.LocalDateTime

@Documentation("Represents get-mistakes response.")
data class MistakesResponse(
    @PropertyDocumentation("document id")
    val documentId: String,
    @PropertyDocumentation("list of <a class='doc-link' href='/docs?section=Mistake'>Mistake</a>")
    val errors: List<Mistake>,
    @PropertyDocumentation("request timestamp")
    val timestamp: LocalDateTime = LocalDateTime.now()
)