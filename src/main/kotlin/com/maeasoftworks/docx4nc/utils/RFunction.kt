package com.maeasoftworks.docx4nc.utils

import com.maeasoftworks.docx4nc.model.MistakeInner
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.RPr

typealias RFunction = (Int, Int, RPr, Boolean, MainDocumentPart) -> MistakeInner?