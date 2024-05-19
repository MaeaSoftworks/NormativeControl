package normativecontrol.core.locales

import java.util.*

enum class Locales(val locale: Locale) {
    EN(Locale.ENGLISH),
    RU(Locale.of("ru", "RU"))
}