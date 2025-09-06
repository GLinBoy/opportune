package com.glinboy.opportune.service

import com.glinboy.opportune.dto.CompanyDTO
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface CompanyService: GenericService<CompanyDTO, UUID> {
}
