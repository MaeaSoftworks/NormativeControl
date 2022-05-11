package com.maeasoftworks.normativecontrol.repositories

import com.maeasoftworks.normativecontrol.entities.DocumentKey
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository

@ConditionalOnBean(DocumentManager::class)
interface DocumentRepository : CrudRepository<DocumentKey, String>