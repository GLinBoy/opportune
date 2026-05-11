package com.glinboy.opportune.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AiAnalysisResultDTO(
	val title: String?,

	@JsonProperty("company_name")
	val companyName: String?,

	val location: String?,

	@JsonProperty("short_description")
	val shortDescription: String?,

	@JsonProperty("cover_letter")
	val coverLetter: String?,

	@JsonProperty("resume_insights")
	val resumeInsights: String?,

	@JsonProperty("interview_introduction")
	val interviewIntroduction: String?,

	@JsonProperty("resume_match")
	val resumeMatch: ResumeMatch,

	@JsonProperty("metadata")
	val metadata: List<MetadataEntry> = emptyList()
) {
	data class ResumeMatch(
		@JsonProperty("overall_score")
		val overallScore: Int?,

		@JsonProperty("skills_match")
		val skillsMatch: ScoreWithRationale,

		@JsonProperty("experience_match")
		val experienceMatch: ScoreWithRationale,

		@JsonProperty("education_match")
		val educationMatch: ScoreWithRationale,

		@JsonProperty("keywords_match")
		val keywordsMatch: ScoreWithRationale
	)

	data class ScoreWithRationale(
		val score: Int?,
		val rationale: String?
	)

	data class MetadataEntry(
		val key: String,
		val value: String?
	)
}

