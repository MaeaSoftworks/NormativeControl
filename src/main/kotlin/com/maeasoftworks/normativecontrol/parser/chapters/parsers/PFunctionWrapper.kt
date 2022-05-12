package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.PPr

//documentId: String, p: Int, isEmpty: Boolean, pPr: PPr, mainDocumentPart: MainDocumentPart
class PFunctionWrapper(val function: (String, Int, Boolean, PPr, MainDocumentPart) -> DocumentError?)