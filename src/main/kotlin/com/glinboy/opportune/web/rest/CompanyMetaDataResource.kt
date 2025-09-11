package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.CompanyMetaDataDTO
import com.glinboy.opportune.service.CompanyMetaDataService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/{company_id}/metadata")
class CompanyMetaDataResource(companyMetaDataService: CompanyMetaDataService) :
	GenericResource<UUID, CompanyMetaDataDTO, CompanyMetaDataService>(companyMetaDataService) {
}

