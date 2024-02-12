package ru.maeasoftworks.normativecontrol.core.rendering

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import ru.maeasoftworks.normativecontrol.core.model.DetailedMistake
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

context(VerificationContext)
fun htmlTemplate(doc: MainDocumentPart?, mistakes: List<DetailedMistake>) = html {
    head {
        script {
            content = lazy { "function mistakes() {return ${Json.encodeToString(mistakes)};}" }
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

                ".page" {
                    width set w?.toDouble()
                    minHeight set h?.toDouble()
                    paddingTop set pageMargins?.top?.intValueExact()?.toDouble()
                    paddingLeft set pageMargins?.left?.intValueExact()?.toDouble()
                    paddingBottom set pageMargins?.bottom?.intValueExact()?.toDouble()
                    paddingRight set pageMargins?.right?.intValueExact()?.toDouble()
                    hyphens set doc?.documentSettingsPart?.jaxbElement?.autoHyphenation?.isVal
                    margin set 10.0
                    backgroundColor set "ffffff"
                }

                ".page-size" {
                    boxShadow set "inset 0px 0px 0px 1px blue"
                    boxSizing set "border-box"
                    position set "absolute"
                    width set pageMargins?.let { (w?.minus(it.left.intValueExact())?.minus(it.right.intValueExact()))?.toDouble() }
                    height set pageMargins?.let { (h?.minus(it.top.intValueExact())?.minus(it.bottom.intValueExact()))?.toDouble() }
                    zIndex set -10.0
                }

                "body" {
                    "display" set "flex"
                    "flex-direction" set "column"
                    backgroundColor set "dedede"
                    "align-items" set "center"
                }
            }
        }
    }
    body {
        label {
            input {
                params {
                    "type" set "checkbox"
                    "onchange" set js("""
                        document.querySelectorAll('*').forEach(x => 
                            x.style.boxShadow = (x.style.boxShadow === '' || x.style.boxShadow === 'none' ? 'inset 0px 0px 0px 1px red' : 'none')
                        );
                        """.trimIndent())
                    +"checked"
                }
            }
            content= "Hide borders"
        }
    }
}