package com.glinboy.opportune.event

import org.springframework.context.ApplicationEvent
import java.util.*

class ApplicationSubmittedEvent(source: Any, val applicationId: UUID) : ApplicationEvent(source)

