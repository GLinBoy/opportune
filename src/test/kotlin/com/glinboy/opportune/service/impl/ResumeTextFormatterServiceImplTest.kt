package com.glinboy.opportune.service.impl

import com.glinboy.opportune.entity.Education
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.SkillGroup
import com.glinboy.opportune.entity.WorkExperience
import com.glinboy.opportune.entity.WorkExperienceBullet
import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.repository.EducationRepository
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.repository.SkillGroupRepository
import com.glinboy.opportune.repository.WorkExperienceRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*

class ResumeTextFormatterServiceImplTest {

	@Test
	fun `formatResumeAsText renders all contact fields`() {
		val profile = Profile(
			id = UUID.randomUUID(),
			email = "john@example.com",
			forename = "John",
			surname = "Doe",
			password = "hashed",
			phone = "+1-555-123-4567",
			linkedinUrl = "https://linkedin.com/in/john",
			portfolioUrl = "https://john.dev",
			jobTitle = "Software Engineer",
			professionalSummary = "Experienced developer with 5 years in Kotlin.",
			status = AccountStatus.ACTIVE,
			roles = setOf(Role.ROLE_USER)
		)

		val profileRepository = mock(ProfileRepository::class.java)
		val workExperienceRepo = mock(WorkExperienceRepository::class.java)
		val educationRepo = mock(EducationRepository::class.java)
		val skillGroupRepo = mock(SkillGroupRepository::class.java)

		`when`(profileRepository.findById(profile.id!!)).thenReturn(Optional.of(profile))
		`when`(workExperienceRepo.findAllByProfileIdOrderByDisplayOrderAsc(profile.id!!)).thenReturn(emptyList())
		`when`(educationRepo.findAllByProfileIdOrderByDisplayOrderAsc(profile.id!!)).thenReturn(emptyList())
		`when`(skillGroupRepo.findAllByProfileIdOrderByDisplayOrderAsc(profile.id!!)).thenReturn(emptyList())

		val service = ResumeTextFormatterServiceImpl(profileRepository, workExperienceRepo, educationRepo, skillGroupRepo)
		val result = service.formatResumeAsText(profile.id!!)

		assertTrue(result.contains("CONTACT INFORMATION"))
		assertTrue(result.contains("Name: John Doe"))
		assertTrue(result.contains("Email: john@example.com"))
		assertTrue(result.contains("Phone: +1-555-123-4567"))
		assertTrue(result.contains("LinkedIn: https://linkedin.com/in/john"))
		assertTrue(result.contains("Portfolio: https://john.dev"))
		assertTrue(result.contains("Job Title: Software Engineer"))
		assertTrue(result.contains("Professional Summary: Experienced developer with 5 years in Kotlin."))
	}

	@Test
	fun `formatResumeAsText renders all sections when populated`() {
		val profileId = UUID.randomUUID()
		val profile = Profile(
			id = profileId,
			email = "jane@example.com",
			forename = "Jane",
			surname = "Smith",
			password = "hashed",
			phone = "555-0000",
			linkedinUrl = "https://linkedin.com/in/jane",
			professionalSummary = "Full-stack developer.",
			status = AccountStatus.ACTIVE,
			roles = setOf(Role.ROLE_USER)
		)

		val workExp = WorkExperience(
			id = UUID.randomUUID(),
			jobTitle = "Senior Developer",
			company = "Acme Corp",
			location = "San Francisco, CA",
			startMonth = 1,
			startYear = 2020,
			endMonth = 6,
			endYear = 2023,
			isCurrent = false,
			displayOrder = 0,
			profile = profile,
			bullets = setOf(
				WorkExperienceBullet(id = UUID.randomUUID(), content = "Built REST APIs", displayOrder = 0),
				WorkExperienceBullet(id = UUID.randomUUID(), content = "Led code reviews", displayOrder = 1)
			)
		)

		val education = Education(
			id = UUID.randomUUID(),
			school = "MIT",
			degree = "Bachelor of Science",
			fieldOfStudy = "Computer Science",
			startYear = 2014,
			endYear = 2018,
			isCurrent = false,
			gpa = "3.9",
			honors = "Summa Cum Laude",
			displayOrder = 0,
			profile = profile,
			courses = mutableListOf("Data Structures", "Algorithms", "Machine Learning")
		)

		val skillGroup = SkillGroup(
			id = UUID.randomUUID(),
			category = "Programming Languages",
			displayOrder = 0,
			profile = profile,
			skills = mutableListOf("Kotlin", "Java", "TypeScript")
		)

		val profileRepository = mock(ProfileRepository::class.java)
		val workExperienceRepo = mock(WorkExperienceRepository::class.java)
		val educationRepo = mock(EducationRepository::class.java)
		val skillGroupRepo = mock(SkillGroupRepository::class.java)

		`when`(profileRepository.findById(profileId)).thenReturn(Optional.of(profile))
		`when`(workExperienceRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(listOf(workExp))
		`when`(educationRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(listOf(education))
		`when`(skillGroupRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(listOf(skillGroup))

		val service = ResumeTextFormatterServiceImpl(profileRepository, workExperienceRepo, educationRepo, skillGroupRepo)
		val result = service.formatResumeAsText(profileId)

		assertTrue(result.contains("CONTACT INFORMATION"))
		assertTrue(result.contains("Name: Jane Smith"))
		assertTrue(result.contains("Full-stack developer."))
		assertTrue(result.contains("WORK EXPERIENCE"))
		assertTrue(result.contains("Senior Developer at Acme Corp, San Francisco, CA (Jan 2020 - Jun 2023)"))
		assertTrue(result.contains("- Built REST APIs"))
		assertTrue(result.contains("- Led code reviews"))
		assertTrue(result.contains("EDUCATION"))
		assertTrue(result.contains("Bachelor of Science in Computer Science, MIT (2014 - 2018)"))
		assertTrue(result.contains("GPA: 3.9"))
		assertTrue(result.contains("Honors: Summa Cum Laude"))
		assertTrue(result.contains("Courses: Data Structures, Algorithms, Machine Learning"))
		assertTrue(result.contains("SKILLS"))
		assertTrue(result.contains("Programming Languages: Kotlin, Java, TypeScript"))
	}

	@Test
	fun `formatResumeAsText renders current position as Present`() {
		val profileId = UUID.randomUUID()
		val profile = Profile(
			id = profileId,
			email = "test@example.com",
			forename = "Test",
			surname = "User",
			password = "hashed",
			status = AccountStatus.ACTIVE,
			roles = setOf(Role.ROLE_USER)
		)

		val workExp = WorkExperience(
			id = UUID.randomUUID(),
			jobTitle = "CTO",
			company = "Startup",
			startMonth = 3,
			startYear = 2022,
			isCurrent = true,
			displayOrder = 0,
			profile = profile
		)

		val currentEducation = Education(
			id = UUID.randomUUID(),
			school = "State University",
			degree = "PhD",
			fieldOfStudy = "AI",
			startYear = 2024,
			isCurrent = true,
			displayOrder = 0,
			profile = profile
		)

		val profileRepository = mock(ProfileRepository::class.java)
		val workExperienceRepo = mock(WorkExperienceRepository::class.java)
		val educationRepo = mock(EducationRepository::class.java)
		val skillGroupRepo = mock(SkillGroupRepository::class.java)

		`when`(profileRepository.findById(profileId)).thenReturn(Optional.of(profile))
		`when`(workExperienceRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(listOf(workExp))
		`when`(educationRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(listOf(currentEducation))
		`when`(skillGroupRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(emptyList())

		val service = ResumeTextFormatterServiceImpl(profileRepository, workExperienceRepo, educationRepo, skillGroupRepo)
		val result = service.formatResumeAsText(profileId)

		assertTrue(result.contains("CTO at Startup (Mar 2022 - Present)"))
		assertTrue(result.contains("PhD in AI, State University (2024 - Present)"))
	}

	@Test
	fun `formatResumeAsText returns empty string when all sections are empty`() {
		val profileId = UUID.randomUUID()
		val profile = Profile(
			id = profileId,
			email = "empty@example.com",
			password = "hashed",
			status = AccountStatus.ACTIVE,
			roles = setOf(Role.ROLE_USER)
		)

		val profileRepository = mock(ProfileRepository::class.java)
		val workExperienceRepo = mock(WorkExperienceRepository::class.java)
		val educationRepo = mock(EducationRepository::class.java)
		val skillGroupRepo = mock(SkillGroupRepository::class.java)

		`when`(profileRepository.findById(profileId)).thenReturn(Optional.of(profile))
		`when`(workExperienceRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(emptyList())
		`when`(educationRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(emptyList())
		`when`(skillGroupRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(emptyList())

		val service = ResumeTextFormatterServiceImpl(profileRepository, workExperienceRepo, educationRepo, skillGroupRepo)
		val result = service.formatResumeAsText(profileId)

		assertEquals("", result)
	}

	@Test
	fun `formatResumeAsText returns empty string when profile not found`() {
		val profileId = UUID.randomUUID()

		val profileRepository = mock(ProfileRepository::class.java)
		val workExperienceRepo = mock(WorkExperienceRepository::class.java)
		val educationRepo = mock(EducationRepository::class.java)
		val skillGroupRepo = mock(SkillGroupRepository::class.java)

		`when`(profileRepository.findById(profileId)).thenReturn(Optional.empty())

		val service = ResumeTextFormatterServiceImpl(profileRepository, workExperienceRepo, educationRepo, skillGroupRepo)
		val result = service.formatResumeAsText(profileId)

		assertEquals("", result)
	}

	@Test
	fun `formatResumeAsText handles missing optional fields gracefully`() {
		val profileId = UUID.randomUUID()
		val profile = Profile(
			id = profileId,
			email = "minimal@example.com",
			forename = "Minimal",
			surname = "User",
			password = "hashed",
			status = AccountStatus.ACTIVE,
			roles = setOf(Role.ROLE_USER)
		)

		val workExp = WorkExperience(
			id = UUID.randomUUID(),
			jobTitle = "Dev",
			company = "Co",
			startYear = 2020,
			displayOrder = 0,
			profile = profile
		)

		val profileRepository = mock(ProfileRepository::class.java)
		val workExperienceRepo = mock(WorkExperienceRepository::class.java)
		val educationRepo = mock(EducationRepository::class.java)
		val skillGroupRepo = mock(SkillGroupRepository::class.java)

		`when`(profileRepository.findById(profileId)).thenReturn(Optional.of(profile))
		`when`(workExperienceRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(listOf(workExp))
		`when`(educationRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(emptyList())
		`when`(skillGroupRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(emptyList())

		val service = ResumeTextFormatterServiceImpl(profileRepository, workExperienceRepo, educationRepo, skillGroupRepo)
		val result = service.formatResumeAsText(profileId)

		assertTrue(result.contains("CONTACT INFORMATION"))
		assertTrue(result.contains("Name: Minimal User"))
		assertTrue(result.contains("Email: minimal@example.com"))
		assertFalse(result.contains("Phone:"))
		assertFalse(result.contains("LinkedIn:"))
		assertFalse(result.contains("Portfolio:"))
		assertFalse(result.contains("Job Title:"))
		assertFalse(result.contains("Professional Summary:"))
		assertTrue(result.contains("WORK EXPERIENCE"))
		assertTrue(result.contains("Dev at Co (2020)"))
		assertFalse(result.contains("EDUCATION"))
		assertFalse(result.contains("SKILLS"))
	}

	@Test
	fun `formatResumeAsText renders year-only date when month is null`() {
		val profileId = UUID.randomUUID()
		val profile = Profile(
			id = profileId,
			email = "test@example.com",
			forename = "Test",
			surname = "User",
			password = "hashed",
			status = AccountStatus.ACTIVE,
			roles = setOf(Role.ROLE_USER)
		)

		val workExp = WorkExperience(
			id = UUID.randomUUID(),
			jobTitle = "Engineer",
			company = "TechCorp",
			startYear = 2019,
			endYear = 2021,
			isCurrent = false,
			displayOrder = 0,
			profile = profile
		)

		val profileRepository = mock(ProfileRepository::class.java)
		val workExperienceRepo = mock(WorkExperienceRepository::class.java)
		val educationRepo = mock(EducationRepository::class.java)
		val skillGroupRepo = mock(SkillGroupRepository::class.java)

		`when`(profileRepository.findById(profileId)).thenReturn(Optional.of(profile))
		`when`(workExperienceRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(listOf(workExp))
		`when`(educationRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(emptyList())
		`when`(skillGroupRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(emptyList())

		val service = ResumeTextFormatterServiceImpl(profileRepository, workExperienceRepo, educationRepo, skillGroupRepo)
		val result = service.formatResumeAsText(profileId)

		assertTrue(result.contains("Engineer at TechCorp (2019 - 2021)"))
	}

	@Test
	fun `formatResumeAsText renders multiple skill groups in order`() {
		val profileId = UUID.randomUUID()
		val profile = Profile(
			id = profileId,
			email = "test@example.com",
			password = "hashed",
			status = AccountStatus.ACTIVE,
			roles = setOf(Role.ROLE_USER)
		)

		val sg1 = SkillGroup(
			id = UUID.randomUUID(),
			category = "Languages",
			displayOrder = 0,
			profile = profile,
			skills = mutableListOf("Kotlin", "Java")
		)
		val sg2 = SkillGroup(
			id = UUID.randomUUID(),
			category = "Tools",
			displayOrder = 1,
			profile = profile,
			skills = mutableListOf("Docker", "Kubernetes")
		)

		val profileRepository = mock(ProfileRepository::class.java)
		val workExperienceRepo = mock(WorkExperienceRepository::class.java)
		val educationRepo = mock(EducationRepository::class.java)
		val skillGroupRepo = mock(SkillGroupRepository::class.java)

		`when`(profileRepository.findById(profileId)).thenReturn(Optional.of(profile))
		`when`(workExperienceRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(emptyList())
		`when`(educationRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(emptyList())
		`when`(skillGroupRepo.findAllByProfileIdOrderByDisplayOrderAsc(profileId)).thenReturn(listOf(sg1, sg2))

		val service = ResumeTextFormatterServiceImpl(profileRepository, workExperienceRepo, educationRepo, skillGroupRepo)
		val result = service.formatResumeAsText(profileId)

		assertTrue(result.contains("SKILLS"))
		assertTrue(result.contains("Languages: Kotlin, Java"))
		assertTrue(result.contains("Tools: Docker, Kubernetes"))
	}
}
