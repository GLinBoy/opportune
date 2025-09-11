package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationResumeDTO
import com.glinboy.opportune.service.ApplicationResumeService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/application-resumes")
class ApplicationResumeResource(applicationResumeService: ApplicationResumeService):
    GenericResource<UUID, ApplicationResumeDTO, ApplicationResumeService>(applicationResumeService) {
}

