package com.maeasoftworks.normativecontrol.dao

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "refresh_tokens")
class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    lateinit var user: User

    @Column(name = "refresh_token", unique = true)
    lateinit var refreshToken: String

    @Column(name = "expiry_date")
    lateinit var expiryDate: Instant
}
