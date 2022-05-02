package com.maeasoftworks.normativecontrol.events

import org.springframework.context.ApplicationEvent


class SaveDocumentEvent(source: Any, val documentId: String) : ApplicationEvent(source)
