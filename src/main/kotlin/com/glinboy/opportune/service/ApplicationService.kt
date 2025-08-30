package com.glinboy.opportune.service

import com.glinboy.opportune.service.dto.ApplicationDTO

interface ApplicationService {

	fun createApplication(applicationDTO: ApplicationDTO): ApplicationDTO

	fun getApplication(id: Long): ApplicationDTO

	fun updateApplication(id: Long, applicationDTO: ApplicationDTO): ApplicationDTO

	fun deleteApplication(id: Long): Unit
}
