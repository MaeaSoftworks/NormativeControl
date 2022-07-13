package com.maeasoftworks.docx4nc.utils

import com.maeasoftworks.docx4nc.model.MistakeInner
import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.wml.PPr

typealias PFunction = (Int, PPr, Boolean, DocumentParser) -> MistakeInner?