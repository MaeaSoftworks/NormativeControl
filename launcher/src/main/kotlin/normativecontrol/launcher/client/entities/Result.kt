package normativecontrol.launcher.client.entities

import org.komapper.annotation.*

@KomapperEntity(["results"])
@KomapperTable("results")
data class Result(
    @KomapperId
    val id: Long,
    @KomapperEnum(EnumType.NAME)
    val status: Status,
    val description: String?
)