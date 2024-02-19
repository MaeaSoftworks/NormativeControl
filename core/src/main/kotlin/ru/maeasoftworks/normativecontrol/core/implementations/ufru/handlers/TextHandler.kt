package ru.maeasoftworks.normativecontrol.core.implementations.ufru.handlers

import org.docx4j.TextUtils
import org.docx4j.wml.Text
import ru.maeasoftworks.normativecontrol.core.abstractions.handlers.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.handlers.HandlerConfig
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile
import ru.maeasoftworks.normativecontrol.core.html.span
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile.globalState

@EagerInitialization
object TextHandler : Handler<Text, Nothing>(
    HandlerConfig.create {
        setHandler { TextHandler }
        setTarget<Text>()
        setProfile(UrFUProfile)
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as Text
        render append span {
            content = TextUtils.getText(element).replace("<", "&lt;").replace(">", "&gt;")
        }
        globalState
    }
}