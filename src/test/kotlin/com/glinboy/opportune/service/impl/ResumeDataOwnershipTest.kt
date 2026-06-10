package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.*
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.service.EducationService
import com.glinboy.opportune.service.SkillGroupService
import com.glinboy.opportune.service.WorkExperienceService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class ResumeDataOwnershipTest {

	@Autowired
	private lateinit var workExperienceService: WorkExperienceService

	@Autowired
	private lateinit var educationService: EducationService

	@Autowired
	private lateinit var skillGroupService: SkillGroupService

	@Autowired
	private lateinit var profileRepository: ProfileRepository

	private lateinit var profileA: Profile
	private lateinit var profileB: Profile

	@BeforeAll
	fun setUp() {
		profileA = profileRepository.saveAndFlush(Profile(
			id = null,
			email = "userA@test.com",
			forename = "User",
			surname = "A",
			password = "hashed-password-a",
			status = AccountStatus.ACTIVE,
			roles = setOf(Role.ROLE_USER)
		))
		profileB = profileRepository.saveAndFlush(Profile(
			id = null,
			email = "userB@test.com",
			forename = "User",
			surname = "B",
			password = "hashed-password-b",
			status = AccountStatus.ACTIVE,
			roles = setOf(Role.ROLE_USER)
		))
	}

	@AfterEach
	fun tearDown() {
		SecurityContextHolder.clearContext()
	}

	private fun setCurrentUser(profile: Profile) {
		val jwt = Jwt.withTokenValue("test-token")
			.subject(profile.id.toString())
			.header("alg", "HS512")
			.claim("roles", listOf("ROLE_USER"))
			.build()
		val authentication = JwtAuthenticationToken(jwt, emptyList())
		SecurityContextHolder.getContext().authentication = authentication
	}

	@Test
	fun `work experience - non-owner cannot read another user's data`() {
		setCurrentUser(profileA)
		val created = workExperienceService.save(WorkExperienceDTO(
			jobTitle = "Engineer",
			company = "TestCorp",
			profileId = profileA.id
		))

		setCurrentUser(profileB)
		val result = workExperienceService.findByIdForCurrentUser(created.id!!)
		assertTrue(result.isEmpty, "Profile B should not see Profile A's work experience")
	}

	@Test
	fun `work experience - non-owner cannot update another user's data`() {
		setCurrentUser(profileA)
		val created = workExperienceService.save(WorkExperienceDTO(
			jobTitle = "Engineer",
			company = "TestCorp",
			profileId = profileA.id
		))

		setCurrentUser(profileB)
		val updatedDto = created.copy(jobTitle = "HACKED")
		assertThrows(NoSuchElementException::class.java) {
			workExperienceService.updateForCurrentUser(updatedDto)
		}
	}

	@Test
	fun `work experience - non-owner cannot delete another user's data`() {
		setCurrentUser(profileA)
		val created = workExperienceService.save(WorkExperienceDTO(
			jobTitle = "Engineer",
			company = "TestCorp",
			profileId = profileA.id
		))

		setCurrentUser(profileB)
		workExperienceService.deleteForCurrentUser(created.id!!)

		setCurrentUser(profileA)
		val stillExists = workExperienceService.findByIdForCurrentUser(created.id!!)
		assertTrue(stillExists.isPresent, "Profile A should still see their own data after B's delete attempt")
	}

	@Test
	fun `education - non-owner cannot read another user's data`() {
		setCurrentUser(profileA)
		val created = educationService.save(EducationDTO(
			school = "MIT",
			degree = "BS",
			fieldOfStudy = "CS",
			profileId = profileA.id
		))

		setCurrentUser(profileB)
		val result = educationService.findByIdForCurrentUser(created.id!!)
		assertTrue(result.isEmpty)
	}

	@Test
	fun `education - non-owner cannot update another user's data`() {
		setCurrentUser(profileA)
		val created = educationService.save(EducationDTO(
			school = "MIT",
			degree = "BS",
			fieldOfStudy = "CS",
			profileId = profileA.id
		))

		setCurrentUser(profileB)
		val updatedDto = created.copy(school = "HACKED")
		assertThrows(NoSuchElementException::class.java) {
			educationService.updateForCurrentUser(updatedDto)
		}
	}

	@Test
	fun `skill group - non-owner cannot read another user's data`() {
		setCurrentUser(profileA)
		val created = skillGroupService.save(SkillGroupDTO(
			category = "Languages",
			profileId = profileA.id,
			skills = listOf("Kotlin", "Java")
		))

		setCurrentUser(profileB)
		val result = skillGroupService.findByIdForCurrentUser(created.id!!)
		assertTrue(result.isEmpty)
	}

	@Test
	fun `skill group - non-owner cannot update another user's data`() {
		setCurrentUser(profileA)
		val created = skillGroupService.save(SkillGroupDTO(
			category = "Languages",
			profileId = profileA.id,
			skills = listOf("Kotlin", "Java")
		))

		setCurrentUser(profileB)
		val updatedDto = created.copy(category = "HACKED")
		assertThrows(NoSuchElementException::class.java) {
			skillGroupService.updateForCurrentUser(updatedDto)
		}
	}

	@Test
	fun `work experience - owner can read and update own data`() {
		setCurrentUser(profileA)
		val created = workExperienceService.save(WorkExperienceDTO(
			jobTitle = "Engineer",
			company = "TestCorp",
			profileId = profileA.id
		))

		val found = workExperienceService.findByIdForCurrentUser(created.id!!)
		assertTrue(found.isPresent)
		assertEquals("Engineer", found.get().jobTitle)

		val updated = workExperienceService.updateForCurrentUser(created.copy(jobTitle = "Senior Engineer"))
		assertEquals("Senior Engineer", updated.jobTitle)
	}

	@Test
	fun `education - CRUD roundtrip for owner`() {
		setCurrentUser(profileA)
		val created = educationService.save(EducationDTO(
			school = "Stanford",
			degree = "MS",
			fieldOfStudy = "AI",
			profileId = profileA.id,
			courses = listOf("Machine Learning", "Deep Learning")
		))

		val found = educationService.findByIdForCurrentUser(created.id!!)
		assertTrue(found.isPresent)
		assertEquals("Stanford", found.get().school)
		assertEquals(2, found.get().courses.size)

		val updated = educationService.updateForCurrentUser(created.copy(
			school = "Stanford University",
			courses = listOf("ML", "DL", "NLP")
		))
		assertEquals("Stanford University", updated.school)
		assertEquals(3, updated.courses.size)
	}

	@Test
	fun `skill group - CRUD roundtrip for owner`() {
		setCurrentUser(profileA)
		val created = skillGroupService.save(SkillGroupDTO(
			category = "Programming",
			profileId = profileA.id,
			skills = listOf("Kotlin", "Java", "TypeScript")
		))

		val found = skillGroupService.findByIdForCurrentUser(created.id!!)
		assertTrue(found.isPresent)
		assertEquals("Programming", found.get().category)
		assertEquals(3, found.get().skills.size)

		val updated = skillGroupService.updateForCurrentUser(created.copy(
			category = "Languages",
			skills = listOf("Kotlin", "Java", "TypeScript", "Python")
		))
		assertEquals("Languages", updated.category)
		assertEquals(4, updated.skills.size)
	}

	@Test
	fun `work experience - bulk replace bullets ownership check`() {
		setCurrentUser(profileA)
		val created = workExperienceService.save(WorkExperienceDTO(
			jobTitle = "Developer",
			company = "Acme",
			profileId = profileA.id,
			bullets = listOf(
				WorkExperienceBulletDTO(content = "Built API"),
				WorkExperienceBulletDTO(content = "Wrote tests")
			)
		))

		setCurrentUser(profileB)
		assertThrows(NoSuchElementException::class.java) {
			workExperienceService.replaceBullets(created.id!!, listOf(
				WorkExperienceBulletDTO(content = "HACKED")
			))
		}
	}

	@Test
	fun `work experience - owner can bulk replace bullets`() {
		setCurrentUser(profileA)
		val created = workExperienceService.save(WorkExperienceDTO(
			jobTitle = "Developer",
			company = "Acme",
			profileId = profileA.id,
			bullets = listOf(
				WorkExperienceBulletDTO(content = "Old bullet 1"),
				WorkExperienceBulletDTO(content = "Old bullet 2")
			)
		))

		val newBullets = workExperienceService.replaceBullets(created.id!!, listOf(
			WorkExperienceBulletDTO(content = "New bullet 1"),
			WorkExperienceBulletDTO(content = "New bullet 2"),
			WorkExperienceBulletDTO(content = "New bullet 3")
		))

		assertEquals(3, newBullets.size)
		assertEquals("New bullet 1", newBullets[0].content)

		val reloaded = workExperienceService.findByIdForCurrentUser(created.id!!)
		assertTrue(reloaded.isPresent)
		assertEquals(3, reloaded.get().bullets.size)
	}

	@Test
	fun `work experience - create with nested bullets`() {
		setCurrentUser(profileA)
		val created = workExperienceService.save(WorkExperienceDTO(
			jobTitle = "Developer",
			company = "Corp",
			profileId = profileA.id,
			bullets = listOf(
				WorkExperienceBulletDTO(content = "Task 1", displayOrder = 0),
				WorkExperienceBulletDTO(content = "Task 2", displayOrder = 1)
			)
		))

		val found = workExperienceService.findByIdForCurrentUser(created.id!!)
		assertTrue(found.isPresent)
		assertEquals(2, found.get().bullets.size)
		assertEquals("Task 1", found.get().bullets[0].content)
		assertEquals("Task 2", found.get().bullets[1].content)
	}
}
