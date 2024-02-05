package ru.maeasoftworks.normativecontrol.core.utils

import org.docx4j.wml.Lvl
import org.docx4j.wml.PPr
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

context(VerificationContext)
val PPr.resolvedNumberingStyle: Lvl?
    get() {
    val numPr = this.getPropertyValue { numPr }
    val numbering = doc.numberingDefinitionsPart.jaxbElement
    val abstractNumId = numbering.num.firstOrNull { it.numId == numPr?.numId?.`val` }?.abstractNumId?.`val`
    val abstract = numbering.abstractNum.firstOrNull { it.abstractNumId == abstractNumId }
    return numPr?.ilvl?.`val`?.toInt()?.let { abstract?.lvl?.get(it) }
}