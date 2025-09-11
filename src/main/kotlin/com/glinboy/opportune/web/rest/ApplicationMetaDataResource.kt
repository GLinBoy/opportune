package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationMetaDataDTO
import com.glinboy.opportune.service.ApplicationMetaDataService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/application-metadata")
class ApplicationMetaDataResource(applicationMetaDataService: ApplicationMetaDataService) :
	GenericResource<UUID, ApplicationMetaDataDTO, ApplicationMetaDataService>(applicationMetaDataService) {
}

