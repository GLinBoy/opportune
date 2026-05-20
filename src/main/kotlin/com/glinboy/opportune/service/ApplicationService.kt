package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.dto.ApplicationDetailsDTO
import com.glinboy.opportune.dto.ApplicationUrlSubmissionDTO
import com.glinboy.opportune.dto.UserDashboardSummaryDTO
import com.glinboy.opportune.projection.ApplicationProjection
import com.glinboy.opportune.projection.ApplicationStatProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface ApplicationService : GenericService<UUID, ApplicationDTO> {
	fun getCompanyApplications(companyId: UUID, pageable: Pageable): Page<ApplicationDTO>
	fun findAllApplications(pageable: Pageable): Page<ApplicationProjection>
	fun getApplicationDetails(id: UUID): Optional<ApplicationDetailsDTO>
	fun getCompanyApplicationsForCurrentUser(companyId: UUID, pageable: Pageable): Page<ApplicationDTO>
	fun findAllApplicationsForCurrentUser(pageable: Pageable): Page<ApplicationProjection>
	fun getApplicationDetailsForCurrentUser(id: UUID): Optional<ApplicationDetailsDTO>
	fun submitApplicationUrl(submission: ApplicationUrlSubmissionDTO): Optional<ApplicationDTO>
	fun getUserSummery(currentUserID: UUID): UserDashboardSummaryDTO
}
