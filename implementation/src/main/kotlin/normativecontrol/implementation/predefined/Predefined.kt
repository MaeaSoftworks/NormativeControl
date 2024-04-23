package normativecontrol.implementation.predefined

import normativecontrol.core.annotations.HandlerGroup
import normativecontrol.core.handlers.HandlerCollection
import normativecontrol.core.handlers.HandlerMapper

@HandlerGroup(HandlerMapper.PREDEFINED_NAME)
internal class Predefined : HandlerCollection(HandlerMapper.PREDEFINED_NAME)