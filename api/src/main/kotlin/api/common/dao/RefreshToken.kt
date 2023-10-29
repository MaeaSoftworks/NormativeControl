package api.common.dao

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "refresh_tokens")
class RefreshToken(
    userId: Long,
    var value: String,
    var expiryDate: Instant
) : Persistable<Long> {
    @Id
    @get:JvmName("getIdKt")
    var id: Long? = null

    var userId: Long? = userId

    @Transient
    override fun getId(): Long? {
        return this.id
    }

    @Transient
    override fun isNew(): Boolean {
        return id == null
    }
}
