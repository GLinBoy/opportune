package com.glinboy.opportune.service.impl

import com.glinboy.opportune.service.ApplicationService
import com.glinboy.opportune.service.dto.ApplicationDTO
import org.springframework.stereotype.Service

@Service
class ApplicationServiceImpl : ApplicationService {

	override fun createApplication(applicationDTO: ApplicationDTO): ApplicationDTO {
		return ApplicationDTO(
			id = 1,
			jobId = applicationDTO.jobId,
			applicantName = applicationDTO.applicantName,
			applicantEmail = applicationDTO.applicantEmail
		)
	}

	override fun getApplication(id: Long): ApplicationDTO {
		return ApplicationDTO(
			id = id,
			jobId = 1,
			applicantName = "John Doe",
			applicantEmail = "john.doe@example.com"
		)
	}

	override fun updateApplication(id: Long, applicationDTO: ApplicationDTO): ApplicationDTO {
		return ApplicationDTO(
			id = id,
			jobId = applicationDTO.jobId,
			applicantName = applicationDTO.applicantName,
			applicantEmail = applicationDTO.applicantEmail
		)
	}

	override fun deleteApplication(id: Long) {
		// Implementation for deleting an application
	}
}
