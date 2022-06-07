package com.maeasoftworks.normativecontrol.dao

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(
    name = "USERS",
    uniqueConstraints = [UniqueConstraint(columnNames = ["USERNAME"]), UniqueConstraint(columnNames = ["EMAIL"])]
)
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "USER_ROLES",
        joinColumns = [JoinColumn(name = "USER_ID")],
        inverseJoinColumns = [JoinColumn(name = "ROLE_ID")]
    )
    var roles: Set<Role> = HashSet()
}
