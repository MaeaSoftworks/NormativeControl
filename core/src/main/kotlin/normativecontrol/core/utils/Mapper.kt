package normativecontrol.core.utils

import org.docx4j.wml.ContentAccessor
import org.docx4j.wml.P
import org.docx4j.wml.R

val ContentAccessor.mapId: Int
    get() = throw NotImplementedError()

val P.mapId: Int
    get() = 1

val R.mapId: Int
    get() = 2