package ru.maeasoftworks.normativecontrol.core.rendering

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart

fun htmlTemplate(doc: MainDocumentPart?) = html {
    head {
        "style" {
            content = css {
                val pageSize = doc?.contents?.body?.sectPr?.pgSz
                val w = pageSize?.w?.intValueExact()
                val h = pageSize?.h?.intValueExact()
                val pageMargins = doc?.contents?.body?.sectPr?.pgMar

                "*" {
                    boxShadow `=` "inset 0px 0px 0px 1px red"
                    boxSizing `=` "border-box"
                    margin `=` 0.0
                    padding `=` 0.0
                }

                ".page" {
                    width `=` w?.toDouble()
                    minHeight `=` h?.toDouble()
                    paddingTop `=` pageMargins?.top?.intValueExact()?.toDouble()
                    paddingLeft `=` pageMargins?.left?.intValueExact()?.toDouble()
                    paddingBottom `=` pageMargins?.bottom?.intValueExact()?.toDouble()
                    paddingRight `=` pageMargins?.right?.intValueExact()?.toDouble()
                    hyphens `=` doc?.documentSettingsPart?.jaxbElement?.autoHyphenation?.isVal
                }

                ".page-size" {
                    boxShadow `=` "inset 0px 0px 0px 1px blue"
                    boxSizing `=` "border-box"
                    position `=` "absolute"
                    width `=` pageMargins?.let { (w?.minus(it.left.intValueExact())?.minus(it.right.intValueExact()))?.toDouble() }
                    height `=` pageMargins?.let { (h?.minus(it.top.intValueExact())?.minus(it.bottom.intValueExact()))?.toDouble() }
                    zIndex `=` -10.0
                }
            }
        }
    }
    body { }
}