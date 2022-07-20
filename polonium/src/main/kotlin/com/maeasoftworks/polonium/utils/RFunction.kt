package com.maeasoftworks.polonium.utils

import com.maeasoftworks.polonium.model.MistakeInner
import com.maeasoftworks.polonium.parsers.DocumentParser
import org.docx4j.wml.RPr

/**
 * Alias for signature of run checking functions in [Rules][com.maeasoftworks.docx4nc.model.Rules]
 */
typealias RFunction = (Int, Int, RPr, Boolean, DocumentParser) -> MistakeInner?