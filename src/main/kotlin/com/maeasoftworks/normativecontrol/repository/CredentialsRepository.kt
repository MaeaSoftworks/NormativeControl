package com.maeasoftworks.normativecontrol.repository

import com.maeasoftworks.normativecontrol.dao.DocumentCredentials
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository

@ConditionalOnBean(DocumentManager::class)
interface CredentialsRepository : CrudRepository<DocumentCredentials, String>