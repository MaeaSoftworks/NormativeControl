package com.maeasoftworks.docx4nc

import com.maeasoftworks.docx4nc.model.MistakeInner
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr

typealias PFunction = (Int, PPr, Boolean, MainDocumentPart) -> MistakeInner?

typealias RFunction = (Int, Int, RPr, Boolean, MainDocumentPart) -> MistakeInner?
