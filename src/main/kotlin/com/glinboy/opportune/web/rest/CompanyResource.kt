package com.glinboy.opportune.web.rest

import com.glinboy.opportune.service.CompanyService
import com.glinboy.opportune.dto.CompanyDTO
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/companies")
class CompanyResource(companyService: CompanyService):
	GenericResource<CompanyDTO, UUID, CompanyService>(companyService){
}
