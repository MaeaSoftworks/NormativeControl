package com.maeasoftworks.tellurium.dao

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User,
    @Column(name = "refresh_token", unique = true)
    var refreshToken: String,
    @Column(name = "expiry_date")
    var expiryDate: Instant
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0
}
