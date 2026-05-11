package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CompanyRepository : JpaRepository<Company, UUID>, JpaSpecificationExecutor<Company> {
	fun findByName(name: String): Optional<Company>
	fun findByNameAndProfileId(name: String, currentUserLoginID: UUID): Optional<Company>
}

