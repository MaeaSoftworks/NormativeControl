package normativecontrol.implementation.predefined

import normativecontrol.core.Core
import normativecontrol.core.annotations.Configuration
import normativecontrol.core.handlers.HandlerCollection

@Configuration(Core.PREDEFINED_NAME)
internal class Predefined : HandlerCollection(Core.PREDEFINED_NAME)