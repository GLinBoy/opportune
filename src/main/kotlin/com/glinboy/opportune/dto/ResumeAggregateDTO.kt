package com.glinboy.opportune.dto

data class ResumeAggregateDTO(
	val workExperiences: List<WorkExperienceDTO> = emptyList(),
	val education: List<EducationDTO> = emptyList(),
	val skillGroups: List<SkillGroupDTO> = emptyList(),
	val projects: List<ResumeProjectDTO> = emptyList(),
	val certifications: List<ResumeCertificationDTO> = emptyList(),
	val languages: List<ResumeLanguageDTO> = emptyList(),
	val volunteerWork: List<VolunteerWorkDTO> = emptyList(),
	val publications: List<ResumePublicationDTO> = emptyList(),
	val awards: List<ResumeAwardDTO> = emptyList(),
	val affiliations: List<ProfessionalAffiliationDTO> = emptyList()
)
