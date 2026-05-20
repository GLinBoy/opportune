package com.glinboy.opportune.projection

interface ScoreSummaryProjection: ProjectionBase {
	fun getAvgResumeScore(): Double?
	fun getAvgSkillScore(): Double?
	fun getAvgExperienceScore(): Double?
	fun getAvgEducationScore(): Double?
	fun getAvgKeywordScore(): Double?
}
