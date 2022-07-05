package com.maeasoftworks.docx4nc.utils

import com.maeasoftworks.docx4nc.model.MistakeInner
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.PPr

typealias PFunction = (Int, PPr, Boolean, MainDocumentPart) -> MistakeInner?