package com.maeasoftworks.core.utils

import com.maeasoftworks.core.model.MistakeInner
import com.maeasoftworks.core.parsers.DocumentParser
import org.docx4j.wml.RPr

/**
 * Alias for signature of run checking functions in [Rules][com.maeasoftworks.core.model.Rules]
 */
typealias RFunction = (p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser) -> MistakeInner?
