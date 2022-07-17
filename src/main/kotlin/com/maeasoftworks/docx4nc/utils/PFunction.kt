package com.maeasoftworks.docx4nc.utils

import com.maeasoftworks.docx4nc.model.MistakeInner
import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.wml.PPr

/**
 * Alias for signature of paragraph checking functions in [Rules][com.maeasoftworks.docx4nc.model.Rules]
 */
typealias PFunction = (Int, PPr, Boolean, DocumentParser) -> MistakeInner?