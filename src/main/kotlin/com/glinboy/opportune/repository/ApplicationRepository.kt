package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Application
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApplicationRepository : JpaRepository<Application, UUID>, JpaSpecificationExecutor<Application> {
	fun findAllByCompanyId(companyId: UUID, pageable: Pageable): Page<Application>
}

