package ru.maeasoftworks.normativecontrol.core.model

import org.docx4j.wml.P
import org.jvnet.jaxb2_commons.ppp.Child

object Transmission {
    suspend fun transmitChild(child: Child?) {
        when (child) {
            is P -> PVerifier.verify(child)
            null -> Unit
        }
    }
}