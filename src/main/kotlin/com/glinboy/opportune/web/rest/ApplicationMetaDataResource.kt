package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationMetaDataDTO
import com.glinboy.opportune.service.ApplicationMetaDataService
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/application-metadata")
class ApplicationMetaDataResource(applicationMetaDataService: ApplicationMetaDataService):
    GenericResource<UUID, ApplicationMetaDataDTO, ApplicationMetaDataService>(applicationMetaDataService) {
}

