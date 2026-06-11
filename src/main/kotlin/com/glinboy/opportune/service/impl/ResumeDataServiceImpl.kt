package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ResumeAggregateDTO
import com.glinboy.opportune.service.*
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ResumeDataServiceImpl(
	private val workExperienceService: WorkExperienceService,
	private val educationService: EducationService,
	private val skillGroupService: SkillGroupService,
	private val resumeProjectService: ResumeProjectService,
	private val resumeCertificationService: ResumeCertificationService,
	private val resumeLanguageService: ResumeLanguageService,
	private val volunteerWorkService: VolunteerWorkService,
	private val resumePublicationService: ResumePublicationService,
	private val resumeAwardService: ResumeAwardService,
	private val professionalAffiliationService: ProfessionalAffiliationService
) : ResumeDataService {

	override fun getAggregateForCurrentUser(): ResumeAggregateDTO {
		val uncapped = PageRequest.of(0, Int.MAX_VALUE)
		return ResumeAggregateDTO(
			workExperiences = workExperienceService.findAllForCurrentUser(uncapped).content,
			education = educationService.findAllForCurrentUser(uncapped).content,
			skillGroups = skillGroupService.findAllForCurrentUser(uncapped).content,
			projects = resumeProjectService.findAllForCurrentUser(uncapped).content,
			certifications = resumeCertificationService.findAllForCurrentUser(uncapped).content,
			languages = resumeLanguageService.findAllForCurrentUser(uncapped).content,
			volunteerWork = volunteerWorkService.findAllForCurrentUser(uncapped).content,
			publications = resumePublicationService.findAllForCurrentUser(uncapped).content,
			awards = resumeAwardService.findAllForCurrentUser(uncapped).content,
			affiliations = professionalAffiliationService.findAllForCurrentUser(uncapped).content
		)
	}
}
