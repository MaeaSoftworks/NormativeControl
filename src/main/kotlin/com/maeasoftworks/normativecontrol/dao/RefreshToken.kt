package com.maeasoftworks.normativecontrol.dao

import java.time.Instant
import javax.persistence.*

@Entity(name = "REFRESH_TOKEN")
class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    lateinit var user: User

    @Column(nullable = false, unique = true)
    lateinit var token: String

    @Column(nullable = false)
    lateinit var expiryDate: Instant
}