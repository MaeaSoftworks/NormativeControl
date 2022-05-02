package com.maeasoftworks.normativecontrol.repositories

import com.maeasoftworks.normativecontrol.daos.DocumentData
import org.springframework.data.repository.CrudRepository

interface DocumentDataRepository : CrudRepository<DocumentData, String>