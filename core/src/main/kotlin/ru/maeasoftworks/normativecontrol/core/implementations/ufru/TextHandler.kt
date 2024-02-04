package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.TextUtils
import org.docx4j.wml.Text
import ru.maeasoftworks.normativecontrol.core.abstractions.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.Mapping
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.rendering.span
import ru.maeasoftworks.normativecontrol.core.utils.verificationContext

@EagerInitialization
object TextHandler: Handler<Text>(Profile.UrFU, Mapping.of { TextHandler }) {
    override suspend fun handle(element: Any): Unit = verificationContext ctx@{
        element as Text
        render.appender append span {
            content = TextUtils.getText(element)
        }
    }
}