package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.entity.Profile
import org.springframework.stereotype.Component
import java.util.*

@Component
class ApplicationMapper: GenericMapper<ApplicationDTO, Application> {
	override fun toEntity(dto: ApplicationDTO): Application {
		return Application(
			id = dto.id ?: UUID.randomUUID(),
			url = dto.url,
			title = dto.title,
			location = dto.location,
			appliedAt = dto.appliedAt,
			salary = dto.salary,
			note = dto.note,
			rawContent = dto.rawContent,
			description = dto.description,
			coverLetter = dto.coverLetter,
			resumeInsights = dto.resumeInsights,
			status = dto.status,
			company = dto.companyId?.let { Company(id = it) },
			profile = dto.profileId?.let { Profile(id = it) },
			// timeline, interviewNotes, metadata, attachments left as defaults
			// resume is not constructed here because ApplicationResume requires an application reference
			resume = null
		)
	}

	override fun toDto(entity: Application): ApplicationDTO {
		return ApplicationDTO(
			id = entity.id,
			url = entity.url,
			title = entity.title,
			location = entity.location,
			appliedAt = entity.appliedAt,
			salary = entity.salary,
			note = entity.note,
			rawContent = entity.rawContent,
			description = entity.description,
			coverLetter = entity.coverLetter,
			resumeInsights = entity.resumeInsights,
			status = entity.status,
			companyId = entity.company?.id,
			profileId = entity.profile?.id,
			resumeId = entity.resume?.id
		)
	}
}
