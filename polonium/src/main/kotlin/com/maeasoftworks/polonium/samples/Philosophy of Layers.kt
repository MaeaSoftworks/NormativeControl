@file:Suppress("unused")

package com.maeasoftworks.polonium.samples

/**
 * Kotlin equivalent for Java source code
 */
private interface ContentAccessor<T> {
    val content: MutableList<T>
}

/**
 * Philosophy of layers:
 *
 * Each document can be represented as collection of elements (most often paragraphs, but there may be some other elements)
 * which in Maea Softworks is named as "p-layer" (from "paragraph layer").
 * @see PSample
 */
private class DocumentSample : ContentAccessor<PSample> {
    /**
     * This is p-layer
     */
    override val content: MutableList<PSample> = mutableListOf()
}

/**
 * Also, most of the elements in p-layer can be represented as collection of order below elements (mostly runs)
 * which we named as "r-layer" (from "run layer" respectively).
 * @see RSample
 */
private class PSample : ContentAccessor<RSample> {
    /**
     * r-layer
     */
    override val content: MutableList<RSample> = mutableListOf()
}

/**
 * And finally, elements in r-layer can also be collections. This is called the "c-layer" ("content layer").
 * @see CSample
 */
private class RSample : ContentAccessor<CSample> {
    /**
     * c-layer
     */
    override val content: MutableList<CSample> = mutableListOf()
}

/**
 * Endpoint of layers.
 */
private class CSample {
    val content: Any? = null
}