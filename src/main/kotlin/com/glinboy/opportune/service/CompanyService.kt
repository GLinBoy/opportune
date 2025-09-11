package com.glinboy.opportune.service

import com.glinboy.opportune.dto.CompanyDTO
import org.springframework.stereotype.Service
import java.util.*

@Service
interface CompanyService: GenericService<UUID, CompanyDTO> {
}
