package normativecontrol.implementation.predefined

import normativecontrol.core.Core
import normativecontrol.core.annotations.HandlerGroup
import normativecontrol.core.handlers.HandlerCollection

@HandlerGroup(Core.PREDEFINED_NAME)
internal class Predefined : HandlerCollection(Core.PREDEFINED_NAME)