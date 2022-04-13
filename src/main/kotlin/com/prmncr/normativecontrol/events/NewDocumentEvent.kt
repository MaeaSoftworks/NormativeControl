package com.prmncr.normativecontrol.events

import org.springframework.context.ApplicationEvent

class NewDocumentEvent(source: Any?, val documentId: String) : ApplicationEvent(source!!)