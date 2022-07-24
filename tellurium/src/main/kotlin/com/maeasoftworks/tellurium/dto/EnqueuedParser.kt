package com.maeasoftworks.tellurium.dto

import com.maeasoftworks.polonium.parsers.DocumentParser

class EnqueuedParser(val documentParser: DocumentParser, val documentDTO: DocumentDTO, var render: String? = null)
