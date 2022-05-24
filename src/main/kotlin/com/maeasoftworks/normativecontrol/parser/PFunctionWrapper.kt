package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.entities.DocumentError
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.PPr

//documentId: String, p: Int, isEmpty: Boolean, pPr: PPr, mainDocumentPart: MainDocumentPart
class PFunctionWrapper(val function: (String, Int, PPr, Boolean, MainDocumentPart) -> DocumentError?)