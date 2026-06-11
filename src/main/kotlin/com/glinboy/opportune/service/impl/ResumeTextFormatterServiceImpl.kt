package com.glinboy.opportune.service.impl

import com.glinboy.opportune.repository.EducationRepository
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.repository.SkillGroupRepository
import com.glinboy.opportune.repository.WorkExperienceRepository
import com.glinboy.opportune.service.ResumeTextFormatterService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ResumeTextFormatterServiceImpl(
	private val profileRepository: ProfileRepository,
	private val workExperienceRepository: WorkExperienceRepository,
	private val educationRepository: EducationRepository,
	private val skillGroupRepository: SkillGroupRepository
) : ResumeTextFormatterService {

	@Transactional(readOnly = true)
	override fun formatResumeAsText(profileId: UUID): String {
		val profile = profileRepository.findById(profileId).orElse(null) ?: return ""

		val workExperiences = workExperienceRepository.findAllByProfileIdOrderByDisplayOrderAsc(profileId)
		val educations = educationRepository.findAllByProfileIdOrderByDisplayOrderAsc(profileId)
		val skillGroups = skillGroupRepository.findAllByProfileIdOrderByDisplayOrderAsc(profileId)

		val sb = StringBuilder()

		appendContactSection(sb, profile)
		appendWorkExperienceSection(sb, workExperiences)
		appendEducationSection(sb, educations)
		appendSkillsSection(sb, skillGroups)

		return sb.toString().trim()
	}

	private fun appendContactSection(sb: StringBuilder, profile: com.glinboy.opportune.entity.Profile) {
		val firstName = profile.forename ?: ""
		val lastName = profile.surname ?: ""
		val name = if (firstName.isNotBlank() || lastName.isNotBlank()) "$firstName $lastName".trim() else null

		if (name == null && profile.phone == null &&
			profile.linkedinUrl == null && profile.portfolioUrl == null &&
			profile.jobTitle == null && profile.professionalSummary == null
		) return

		sb.appendLine("CONTACT INFORMATION")
		if (name != null) sb.appendLine("Name: $name")
		profile.email?.let { sb.appendLine("Email: $it") }
		profile.phone?.let { sb.appendLine("Phone: $it") }
		profile.linkedinUrl?.let { sb.appendLine("LinkedIn: $it") }
		profile.portfolioUrl?.let { sb.appendLine("Portfolio: $it") }
		profile.jobTitle?.let { sb.appendLine("Job Title: $it") }
		profile.professionalSummary?.let { sb.appendLine("Professional Summary: $it") }
		sb.appendLine()
	}

	private fun appendWorkExperienceSection(
		sb: StringBuilder,
		experiences: List<com.glinboy.opportune.entity.WorkExperience>
	) {
		if (experiences.isEmpty()) return

		sb.appendLine("WORK EXPERIENCE")
		for (we in experiences) {
			val title = we.jobTitle ?: ""
			val company = we.company ?: ""
			val location = we.location?.let { ", $it" } ?: ""
			val dates = formatDateRange(we.startMonth, we.startYear, we.endMonth, we.endYear, we.isCurrent)

			sb.appendLine("$title at $company$location$dates")

			we.bullets.sortedBy { it.displayOrder }.forEach { bullet ->
				bullet.content?.let { sb.appendLine("- $it") }
			}
		}
		sb.appendLine()
	}

	private fun appendEducationSection(
		sb: StringBuilder,
		educations: List<com.glinboy.opportune.entity.Education>
	) {
		if (educations.isEmpty()) return

		sb.appendLine("EDUCATION")
		for (edu in educations) {
			val degree = edu.degree ?: ""
			val field = edu.fieldOfStudy?.let { " in $it" } ?: ""
			val school = edu.school ?: ""
			val dates = formatYearRange(edu.startYear, edu.endYear, edu.isCurrent)

			sb.appendLine("$degree$field, $school$dates")
			edu.gpa?.let { sb.appendLine("GPA: $it") }
			edu.honors?.let { sb.appendLine("Honors: $it") }
			if (edu.courses.isNotEmpty()) {
				sb.appendLine("Courses: ${edu.courses.joinToString(", ")}")
			}
		}
		sb.appendLine()
	}

	private fun appendSkillsSection(
		sb: StringBuilder,
		skillGroups: List<com.glinboy.opportune.entity.SkillGroup>
	) {
		if (skillGroups.isEmpty()) return

		sb.appendLine("SKILLS")
		for (sg in skillGroups) {
			sg.category?.let { category ->
				sb.appendLine("$category: ${sg.skills.joinToString(", ")}")
			}
		}
		sb.appendLine()
	}

	private fun formatDateRange(
		startMonth: Short?, startYear: Short?,
		endMonth: Short?, endYear: Short?,
		isCurrent: Boolean
	): String {
		if (startYear == null) return ""
		val start = if (startMonth != null) "${monthAbbr(startMonth)} $startYear" else "$startYear"
		val end = if (isCurrent) "Present" else {
			if (endYear == null) ""
			else if (endMonth != null) "${monthAbbr(endMonth)} $endYear" else "$endYear"
		}
		return if (end.isEmpty()) " ($start)" else " ($start - $end)"
	}

	private fun formatYearRange(
		startYear: Short?, endYear: Short?, isCurrent: Boolean
	): String {
		if (startYear == null) return ""
		val end = if (isCurrent) "Present" else endYear?.toString() ?: ""
		return if (end.isEmpty()) " ($startYear)" else " ($startYear - $end)"
	}

	private fun monthAbbr(month: Short): String = MONTHS.getOrElse(month.toInt() - 1) { "" }

	companion object {
		private val MONTHS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
	}
}
