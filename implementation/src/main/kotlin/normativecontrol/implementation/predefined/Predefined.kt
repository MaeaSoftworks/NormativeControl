package normativecontrol.implementation.predefined

import normativecontrol.core.handlers.HandlerCollection
import normativecontrol.core.handlers.HandlerMapper
import normativecontrol.core.annotations.HandlerGroup

@HandlerGroup(HandlerMapper.PREDEFINED_NAME)
internal class Predefined : HandlerCollection(HandlerMapper.PREDEFINED_NAME)