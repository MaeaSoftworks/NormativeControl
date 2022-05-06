package com.maeasoftworks.normativecontrol.events

import org.springframework.context.ApplicationEvent

class AddDocumentToQueueEvent(source: Any, val id: String) : ApplicationEvent(source)