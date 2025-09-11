package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.CompanyDTO
import com.glinboy.opportune.service.CompanyService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/companies")
class CompanyResource(companyService: CompanyService) :
	GenericResource<UUID, CompanyDTO, CompanyService>(companyService) {
}
