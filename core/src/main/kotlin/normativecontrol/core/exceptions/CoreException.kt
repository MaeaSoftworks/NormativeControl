package normativecontrol.core.exceptions

import normativecontrol.core.locales.Locales
import java.util.*

/**
 * Localized exceptions from core module.
 * TODO: write localized causes to database on startup and resolve at api level
 */
sealed class CoreException(
    locale: Locales,
    private val messageCode: String,
    cause: Throwable? = null
) : Exception(cause) {
    private val bundle = ResourceBundle.getBundle("messages/exceptions", locale.locale)

    override fun getLocalizedMessage(): String {
        return bundle.getString(messageCode)
    }

    class Unknown(locale: Locales) : CoreException(locale, Unknown::class.simpleName!!)

    class Timeout(locale: Locales) : CoreException(locale, Timeout::class.simpleName!!)

    class IncorrectDocumentPart(locale: Locales) : CoreException(locale, IncorrectDocumentPart::class.simpleName!!)

    class IncorrectStylesPart(locale: Locales) : CoreException(locale, IncorrectStylesPart::class.simpleName!!)
}