package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ResumeAggregateDTO
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.EducationService
import com.glinboy.opportune.service.ResumeDataService
import com.glinboy.opportune.service.SkillGroupService
import com.glinboy.opportune.service.WorkExperienceService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ResumeDataServiceImpl(
	private val workExperienceService: WorkExperienceService,
	private val educationService: EducationService,
	private val skillGroupService: SkillGroupService
) : ResumeDataService {

	override fun getAggregateForCurrentUser(): ResumeAggregateDTO {
		val uncapped = PageRequest.of(0, Int.MAX_VALUE)
		return ResumeAggregateDTO(
			workExperiences = workExperienceService.findAllForCurrentUser(uncapped).content,
			education = educationService.findAllForCurrentUser(uncapped).content,
			skillGroups = skillGroupService.findAllForCurrentUser(uncapped).content
		)
	}
}
