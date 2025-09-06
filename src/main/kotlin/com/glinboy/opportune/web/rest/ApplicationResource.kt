package com.glinboy.opportune.web.rest

import com.glinboy.opportune.service.ApplicationService
import com.glinboy.opportune.dto.ApplicationDTO
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/applications")
class ApplicationResource(applicationService: ApplicationService):
	GenericResource<ApplicationDTO, UUID, ApplicationService>(applicationService) {
}
