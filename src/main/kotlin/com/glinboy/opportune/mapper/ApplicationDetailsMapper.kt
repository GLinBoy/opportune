package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ApplicationDetailsDTO
import com.glinboy.opportune.entity.Application
import org.springframework.stereotype.Component

@Component
class ApplicationDetailsMapper(
	private val companyMapper: CompanyMapper,
	private val applicationTimelineMapper: ApplicationTimelineMapper,
	private val interviewNoteMapper: InterviewNoteMapper,
	private val applicationMetaDataMapper: ApplicationMetaDataMapper,
	private val applicationResumeMapper: ApplicationResumeMapper,
	private val applicationAttachmentMapper: ApplicationAttachmentMapper,
) : GenericMapper<ApplicationDetailsDTO, Application> {
	override fun createEntity(dto: ApplicationDetailsDTO): Application {
		throw UnsupportedOperationException("This operation is not supported for ApplicationDetailsDTO")
	}

	override fun updateEntity(dto: ApplicationDetailsDTO, entity: Application): Application {
		throw UnsupportedOperationException("This operation is not supported for ApplicationDetailsDTO")
	}

	override fun toDto(entity: Application): ApplicationDetailsDTO {
		return ApplicationDetailsDTO(
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
			company = entity.company?.let { companyMapper.toDto(it) },
			timeline = entity.timeline.map { t -> applicationTimelineMapper.toDto(t) }.toSet(),
			interviewNotes = entity.interviewNotes.map { i -> interviewNoteMapper.toDto(i) }.toSet(),
			metadata = entity.metadata.map { m -> applicationMetaDataMapper.toDto(m) }.toSet(),
			resume = entity.resume?.let { applicationResumeMapper.toDto(it) },
			attachments = entity.attachments.map { a -> applicationAttachmentMapper.toDto(a) }.toSet(),
		)
	}
}
