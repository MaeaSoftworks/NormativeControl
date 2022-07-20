package com.maeasoftworks.polonium.utils

import com.maeasoftworks.polonium.model.MistakeInner
import com.maeasoftworks.polonium.parsers.DocumentParser
import org.docx4j.wml.PPr

/**
 * Alias for signature of paragraph checking functions in [Rules][com.maeasoftworks.docx4nc.model.Rules]
 */
typealias PFunction = (Int, PPr, Boolean, DocumentParser) -> MistakeInner?