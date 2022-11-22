package com.maeasoftworks.polonium.utils

import com.maeasoftworks.polonium.model.MistakeInner
import com.maeasoftworks.polonium.parsers.DocumentParser
import org.docx4j.wml.RPr

/**
 * Alias for signature of run checking functions in [Rules][com.maeasoftworks.polonium.model.Rules]
 */
typealias RFunction = (p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser) -> MistakeInner?