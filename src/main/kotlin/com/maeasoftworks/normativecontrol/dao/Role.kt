package com.maeasoftworks.normativecontrol.dao

import com.maeasoftworks.normativecontrol.dto.RoleType
import javax.persistence.*


@Entity
@Table(name = "ROLES")
class Role(
    @Enumerated(EnumType.STRING)
    var name: RoleType? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}