package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.*
import com.glinboy.opportune.service.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/api/profiles/resume")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class ProfileResumeDataResource(
	private val resumeDataService: ResumeDataService,
	private val workExperienceService: WorkExperienceService,
	private val educationService: EducationService,
	private val skillGroupService: SkillGroupService,
	private val documentParserService: DocumentParserService,
	private val profileResumeService: ProfileResumeService,
	private val fileService: FileService
) {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	@PostMapping("/extract")
	fun extractFromResume(): ResponseEntity<ResumeExtractionResultDTO> {
		val resumeDTO = profileResumeService.findForCurrentUser()
			.orElseThrow { NoSuchElementException("No resume upload found for the current user") }

		val (resource, _) = profileResumeService.getFileResourceForCurrentUser()

		val resumeText = resource.inputStream.use { inputStream ->
			documentParserService.extractText(inputStream, resumeDTO.contentType ?: "application/pdf")
		}

		val result = documentParserService.parseResumeData(resumeText)
		return ResponseEntity.ok(result)
	}

	@GetMapping
	fun getAggregate(): ResponseEntity<ResumeAggregateDTO> =
		ResponseEntity.ok(resumeDataService.getAggregateForCurrentUser())

	@GetMapping("/work-experiences")
	fun getWorkExperiences(): ResponseEntity<List<WorkExperienceDTO>> =
		ResponseEntity.ok(workExperienceService.findAllForCurrentUser(
			org.springframework.data.domain.PageRequest.of(0, Int.MAX_VALUE)
		).content)

	@GetMapping("/work-experiences/{id}")
	fun getWorkExperience(@PathVariable id: UUID): ResponseEntity<WorkExperienceDTO> =
		ResponseEntity.ok(workExperienceService.getByIdForCurrentUser(id))

	@PostMapping("/work-experiences")
	fun createWorkExperience(
		@Valid @RequestBody dto: WorkExperienceDTO,
		request: HttpServletRequest
	): ResponseEntity<WorkExperienceDTO> {
		val saved = workExperienceService.save(dto)
		return ResponseEntity.created(URI.create("${request.requestURI}/${saved.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/work-experiences/{id}")
	fun updateWorkExperience(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: WorkExperienceDTO
	): ResponseEntity<WorkExperienceDTO> {
		val updated = workExperienceService.updateForCurrentUser(dto.copy(id = id))
		return ResponseEntity.ok(updated)
	}

	@DeleteMapping("/work-experiences/{id}")
	fun deleteWorkExperience(@PathVariable id: UUID): ResponseEntity<Unit> {
		workExperienceService.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}

	@PutMapping("/work-experiences/{id}/bullets")
	fun replaceBullets(
		@PathVariable id: UUID,
		@Valid @RequestBody bullets: List<WorkExperienceBulletDTO>
	): ResponseEntity<List<WorkExperienceBulletDTO>> {
		val replaced = workExperienceService.replaceBullets(id, bullets)
		return ResponseEntity.ok(replaced)
	}

	@GetMapping("/education")
	fun getEducation(): ResponseEntity<List<EducationDTO>> =
		ResponseEntity.ok(educationService.findAllForCurrentUser(
			org.springframework.data.domain.PageRequest.of(0, Int.MAX_VALUE)
		).content)

	@GetMapping("/education/{id}")
	fun getEducationEntry(@PathVariable id: UUID): ResponseEntity<EducationDTO> =
		ResponseEntity.ok(educationService.getByIdForCurrentUser(id))

	@PostMapping("/education")
	fun createEducationEntry(
		@Valid @RequestBody dto: EducationDTO,
		request: HttpServletRequest
	): ResponseEntity<EducationDTO> {
		val saved = educationService.save(dto)
		return ResponseEntity.created(URI.create("${request.requestURI}/${saved.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/education/{id}")
	fun updateEducationEntry(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: EducationDTO
	): ResponseEntity<EducationDTO> {
		val updated = educationService.updateForCurrentUser(dto.copy(id = id))
		return ResponseEntity.ok(updated)
	}

	@DeleteMapping("/education/{id}")
	fun deleteEducationEntry(@PathVariable id: UUID): ResponseEntity<Unit> {
		educationService.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}

	@GetMapping("/skill-groups")
	fun getSkillGroups(): ResponseEntity<List<SkillGroupDTO>> =
		ResponseEntity.ok(skillGroupService.findAllForCurrentUser(
			org.springframework.data.domain.PageRequest.of(0, Int.MAX_VALUE)
		).content)

	@GetMapping("/skill-groups/{id}")
	fun getSkillGroup(@PathVariable id: UUID): ResponseEntity<SkillGroupDTO> =
		ResponseEntity.ok(skillGroupService.getByIdForCurrentUser(id))

	@PostMapping("/skill-groups")
	fun createSkillGroup(
		@Valid @RequestBody dto: SkillGroupDTO,
		request: HttpServletRequest
	): ResponseEntity<SkillGroupDTO> {
		val saved = skillGroupService.save(dto)
		return ResponseEntity.created(URI.create("${request.requestURI}/${saved.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/skill-groups/{id}")
	fun updateSkillGroup(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: SkillGroupDTO
	): ResponseEntity<SkillGroupDTO> {
		val updated = skillGroupService.updateForCurrentUser(dto.copy(id = id))
		return ResponseEntity.ok(updated)
	}

	@DeleteMapping("/skill-groups/{id}")
	fun deleteSkillGroup(@PathVariable id: UUID): ResponseEntity<Unit> {
		skillGroupService.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}
}
