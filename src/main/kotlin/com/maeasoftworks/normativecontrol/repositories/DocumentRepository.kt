package com.maeasoftworks.normativecontrol.repositories

import com.maeasoftworks.normativecontrol.daos.DocumentKey
import org.springframework.data.repository.CrudRepository

interface DocumentRepository : CrudRepository<DocumentKey, String>