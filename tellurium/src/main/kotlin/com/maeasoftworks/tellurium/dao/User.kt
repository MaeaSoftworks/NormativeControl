package com.maeasoftworks.tellurium.dao

import com.maeasoftworks.tellurium.dto.RoleType
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["email"])])
class User(
    @NotBlank
    @Size(max = 20)
    var username: String,
    @NotBlank
    @Size(max = 50)
    @Email
    var email: String,
    @NotBlank
    @Size(max = 120)
    var password: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ElementCollection(targetClass = RoleType::class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    lateinit var roles: Set<RoleType>
}
