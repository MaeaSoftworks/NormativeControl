package com.maeasoftworks.core.utils

import com.maeasoftworks.core.model.MistakeInner
import com.maeasoftworks.core.parsers.DocumentParser
import org.docx4j.wml.PPr

/**
 * Alias for signature of paragraph checking functions in [Rules][com.maeasoftworks.core.model.Rules]
 */
typealias PFunction = (p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser) -> MistakeInner?
