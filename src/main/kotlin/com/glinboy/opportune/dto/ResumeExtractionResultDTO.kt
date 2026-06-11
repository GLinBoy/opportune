package com.glinboy.opportune.dto

data class ResumeExtractionResultDTO(
    val workExperiences: List<WorkExperienceDTO> = emptyList(),
    val education: List<EducationDTO> = emptyList(),
    val skillGroups: List<SkillGroupDTO> = emptyList()
)
