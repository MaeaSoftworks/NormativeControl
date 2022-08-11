# Polonium

![Polonium](src/main/resources/polonium.svg)

Mistake search library for Normative Control

Powered by [docx4j](https://www.docx4java.org/trac/docx4j)

# Quick Guide

## Layers

Each document can be represented as collection of elements (most often paragraphs, but there may be some other elements)
which we named as "p-layer" (from "paragraph layer").

For example let's create some interface:

```kotlin
private interface ContentAccessor<T> {
    val content: MutableList<T>
}
```

In that case class of document can look like this:

```kotlin
private class DocumentSample : ContentAccessor<PSample> {
    // This is p-layer
    override val content: MutableList<PSample> = mutableListOf()
}
```

Also, most of the elements in p-layer can be represented as collection of order below elements (mostly runs)
which we named as "r-layer" (from "run layer" respectively).

```kotlin
private class PSample : ContentAccessor<RSample> {
    // r-layer or p-content
    override val content: MutableList<RSample> = mutableListOf()
}
```

And finally, elements in r-layer can also be collections. This is called the "c-layer" ("content layer").

```kotlin
private class RSample : ContentAccessor<CSample> {
    // c-layer or r-content
    override val content: MutableList<CSample> = mutableListOf()
}
```

And, finally, endpoint of layers.

```kotlin
private class CSample { 
    val content: Any? = null
}
```