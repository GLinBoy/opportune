package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.entity.Company
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.enums.ApplicationStatus
import com.glinboy.opportune.security.SecurityUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ApplicationMapper : GenericMapper<ApplicationDTO, Application> {
	override fun createEntity(dto: ApplicationDTO): Application {
		return Application(
			id = null,
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
			interviewIntroduction = dto.interviewIntroduction,
			resumeOverallScore = dto.resumeOverallScore,
			skillMatchScore = dto.skillMatchScore,
			skillMatchRationale = dto.skillMatchRationale,
			experienceMatchScore = dto.experienceMatchScore,
			experienceMatchRationale = dto.experienceMatchRationale,
			educationMatchScore = dto.educationMatchScore,
			educationMatchRationale = dto.educationMatchRationale,
			keywordMatchScore = dto.keywordMatchScore,
			keywordMatchRationale = dto.keywordMatchRationale,
			status = ApplicationStatus.INITIATED,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
			company = dto.companyId?.let { Company(id = it) },
			profile = dto.profileId?.let { Profile(id = it) } ?: Profile(id = SecurityUtils.getCurrentUserLoginID()),
		)
	}

	override fun updateEntity(dto: ApplicationDTO, entity: Application): Application {
		return entity.copy(
			id = entity.id,
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
			interviewIntroduction = dto.interviewIntroduction,
			resumeOverallScore = dto.resumeOverallScore,
			skillMatchScore = dto.skillMatchScore,
			skillMatchRationale = dto.skillMatchRationale,
			experienceMatchScore = dto.experienceMatchScore,
			experienceMatchRationale = dto.experienceMatchRationale,
			educationMatchScore = dto.educationMatchScore,
			educationMatchRationale = dto.educationMatchRationale,
			keywordMatchScore = dto.keywordMatchScore,
			keywordMatchRationale = dto.keywordMatchRationale,
			status = dto.status,
			createdDate = entity.createdDate,
			lastModifiedDate = Instant.now(),
			// Use existing entity relationships if they exist, otherwise create from DTO IDs
			company = dto.companyId?.let { Company(id = it) },
			profile = entity.profile,
			// Preserve existing collections
			timeline = entity.timeline,
			interviewNotes = entity.interviewNotes,
			metadata = entity.metadata,
			resume = entity.resume,
			attachments = entity.attachments,
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
			interviewIntroduction = entity.interviewIntroduction,
			resumeOverallScore = entity.resumeOverallScore,
			skillMatchScore = entity.skillMatchScore,
			skillMatchRationale = entity.skillMatchRationale,
			experienceMatchScore = entity.experienceMatchScore,
			experienceMatchRationale = entity.experienceMatchRationale,
			educationMatchScore = entity.educationMatchScore,
			educationMatchRationale = entity.educationMatchRationale,
			keywordMatchScore = entity.keywordMatchScore,
			keywordMatchRationale = entity.keywordMatchRationale,
			status = entity.status,
			createdDate = entity.createdDate,
			lastModifiedDate = entity.lastModifiedDate,
			companyId = entity.company?.id,
			profileId = entity.profile?.id,
			resumeId = entity.resume?.id,
		)
	}
}
