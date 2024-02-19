package ru.maeasoftworks.normativecontrol.core.html

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import ru.maeasoftworks.normativecontrol.core.abstractions.mistakes.MistakeSerializer
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import ru.maeasoftworks.normativecontrol.core.utils.lazySerializable

context(VerificationContext)
fun htmlTemplate(doc: MainDocumentPart?, mistakes: MistakeSerializer) = html {
    head {
        script {
            content = lazySerializable { mistakes.serialize() }
        }

        style {
            content = css {
                val pageSize = doc?.contents?.body?.sectPr?.pgSz
                val w = pageSize?.w?.intValueExact()
                val h = pageSize?.h?.intValueExact()
                val pageMargins = doc?.contents?.body?.sectPr?.pgMar

                "*" {
                    boxSizing set "border-box"
                    margin set 0.0
                    padding set 0.0
                }

                "body" {
                    "display" set "grid"
                    "grid-template-rows" set "auto auto"
                    "height" set "100vh"
                }

                ".page" {
                    hyphens set doc?.documentSettingsPart?.jaxbElement?.autoHyphenation?.isVal
                    margin set 10.0
                    backgroundColor set "white"
                }

                ".page-size" {
                    boxShadow set "inset 0px 0px 0px 1px blue"
                    boxSizing set "border-box"
                    position set "absolute"
                    width set pageMargins?.let { (w?.minus(it.left.intValueExact())?.minus(it.right.intValueExact()))?.toDouble() }
                    height set pageMargins?.let { (h?.minus(it.top.intValueExact())?.minus(it.bottom.intValueExact()))?.toDouble() }
                    zIndex set -10.0
                }

                ".container" {
                    "display" set "flex"
                    "flex-direction" set "column"
                    backgroundColor set "dedede"
                    "align-items" set "center"
                    "overflow" set "auto"
                }

                ".bordered *" {
                    "box-shadow" set "inset 0px 0px 0px 1px red"
                }
            }
        }
    }
    body {
        div {
            classes += "render-settings"
            p {
                content = "Render settings"
            }
            br()
            label {
                input {
                    params {
                        "type" set "checkbox"
                        "onchange" set js("document.querySelector('$CONTAINER_SELECTOR').classList.toggle('bordered');")
                        +"checked"
                    }
                }
                content = "Hide borders"
            }
        }
        div {
            classes += CONTAINER_CLASS_NAME
        }
    }
}

const val CONTAINER_CLASS_NAME = "container"
const val CONTAINER_SELECTOR = ".$CONTAINER_CLASS_NAME"