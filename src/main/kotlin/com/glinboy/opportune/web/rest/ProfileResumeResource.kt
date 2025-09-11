package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ProfileResumeDTO
import com.glinboy.opportune.service.ProfileResumeService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/profile-resumes")
class ProfileResumeResource(profileResumeService: ProfileResumeService) :
	GenericResource<UUID, ProfileResumeDTO, ProfileResumeService>(profileResumeService) {
}

