package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.service.ApplicationService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/applications")
class ApplicationResource(applicationService: ApplicationService):
	GenericResource<UUID, ApplicationDTO, ApplicationService>(applicationService) {
}
