package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationTimelineDTO
import com.glinboy.opportune.service.ApplicationTimelineService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/application-timelines")
class ApplicationTimelineResource(applicationTimelineService: ApplicationTimelineService):
    GenericResource<UUID, ApplicationTimelineDTO, ApplicationTimelineService>(applicationTimelineService) {
}

