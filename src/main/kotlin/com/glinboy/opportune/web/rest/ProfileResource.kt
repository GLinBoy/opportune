package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.service.ProfileService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/profiles")
class ProfileResource(profileService: ProfileService) :
	GenericResource<UUID, ProfileDTO, ProfileService>(profileService) {
}

