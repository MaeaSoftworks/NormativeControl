package normativecontrol.core

import normativecontrol.core.contexts.RenderingContext
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.exceptions.CoreException
import normativecontrol.core.locales.Locales
import normativecontrol.core.mistakes.MistakeSerializer
import normativecontrol.core.mocktypes.Metadata
import normativecontrol.core.rendering.css.set
import normativecontrol.core.rendering.html.*
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Verified document representation.
 * @param runtime verification runtime
 * @param file source file
 * @param locale error localization locale
 */
internal class Document(runtime: Runtime, file: InputStream, locale: Locales) {
    /**
     * Count of mistakes found in document.
     */
    val mistakeCount: Int
        get() = ctx.lastMistakeId.toInt()

    /**
     * Shortcut to rendered version of document.
     */
    val render: String
        get() = ctx.render.render()

    private val mlPackage: WordprocessingMLPackage = WordprocessingMLPackage.load(file)
    private val ctx = VerificationContext(runtime, mlPackage, locale)

    init {
        runtime.context = ctx
    }

    /**
     * Verification start.
     */
    internal fun runVerification() {
        with(ctx) {
            // region metadata verification
            val metadata = Metadata((doc.`package` as WordprocessingMLPackage).docPropsCorePart?.contents)
            runtime.handlers[metadata]?.handleElement(metadata)
            // endregion
            if (doc.content.isEmpty()) {
                throw CoreException.IncorrectDocumentPart(locale)
            }

            doc.content.iterate { element, _ ->
                runtime.handlers[element]?.handleElement(element)
            }
        }
    }

    /**
     * Save result file (with comments).
     * @param stream stream to file writing
     */
    internal fun writeResult(stream: ByteArrayOutputStream) {
        mlPackage.save(stream)
    }

    companion object {
        context(RenderingContext)
        internal fun createInitialHtmlMarkup(doc: MainDocumentPart?, mistakes: MistakeSerializer): HtmlElement {
            return html {
                head {
                    script {
                        content = LazySerializable { mistakes.serialize() }
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
                                "white-space" set "pre-wrap"
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
                                backgroundColor set "#dedede"
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
                                attributes {
                                    "type" set "checkbox"
                                    "onchange" set js("document.querySelector('${Constants.CONTAINER_SELECTOR}').classList.toggle('bordered');")
                                    +"checked"
                                }
                            }
                            content = "Hide borders"
                        }
                    }
                    div {
                        classes += Constants.CONTAINER_CLASS_NAME
                    }
                }
            }
        }
    }
}