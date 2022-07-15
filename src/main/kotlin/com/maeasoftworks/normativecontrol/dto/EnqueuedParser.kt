package com.maeasoftworks.normativecontrol.dto

import com.maeasoftworks.docx4nc.parsers.DocumentParser

class EnqueuedParser(val documentParser: DocumentParser, val document: Document, var render: String? = null)
