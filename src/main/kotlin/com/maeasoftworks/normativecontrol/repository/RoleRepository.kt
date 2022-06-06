package com.maeasoftworks.normativecontrol.repository

import com.maeasoftworks.normativecontrol.dao.Role
import com.maeasoftworks.normativecontrol.dto.RoleType
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RoleRepository : CrudRepository<Role, String> {
    fun findByName(name: RoleType): Optional<Role>
}