package normativecontrol.core.components

import normativecontrol.core.handlers.AbstractHandler
import org.docx4j.TextUtils

abstract class TextContainer<H: AbstractHandler<*>>(handler: H) {
    var value: String? = null
    var isBlank: Boolean? = null

    init {
        handler.hooks.beforeHandle.subscribe { element ->
            if (value != null) return@subscribe
            value = cacheText(element!!)
        }
        handler.hooks.afterHandle.subscribe {
            value = null
            isBlank = null
        }
    }

    fun cacheText(element: Any): String {
        if (value == null) {
            value = TextUtils.getText(element)
            isBlank = value!!.isBlank()
        }
        afterTextCached()
        return value!!
    }

    open fun afterTextCached() { }
}