package ru.maeasoftworks.normativecontrol.core.model

import org.jvnet.jaxb2_commons.ppp.Child

interface Verifier<T: Child> {
    suspend fun verify(child: T)
}