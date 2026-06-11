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
	private val fileService: FileService,
	private val resumeProjectService: ResumeProjectService,
	private val resumeCertificationService: ResumeCertificationService,
	private val resumeLanguageService: ResumeLanguageService,
	private val volunteerWorkService: VolunteerWorkService,
	private val resumePublicationService: ResumePublicationService,
	private val resumeAwardService: ResumeAwardService,
	private val professionalAffiliationService: ProfessionalAffiliationService
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

	@GetMapping("/projects")
	fun getProjects(): ResponseEntity<List<ResumeProjectDTO>> =
		ResponseEntity.ok(resumeProjectService.findAllForCurrentUser(
			org.springframework.data.domain.PageRequest.of(0, Int.MAX_VALUE)
		).content)

	@GetMapping("/projects/{id}")
	fun getProject(@PathVariable id: UUID): ResponseEntity<ResumeProjectDTO> =
		ResponseEntity.ok(resumeProjectService.getByIdForCurrentUser(id))

	@PostMapping("/projects")
	fun createProject(
		@Valid @RequestBody dto: ResumeProjectDTO,
		request: HttpServletRequest
	): ResponseEntity<ResumeProjectDTO> {
		val saved = resumeProjectService.save(dto)
		return ResponseEntity.created(URI.create("${request.requestURI}/${saved.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/projects/{id}")
	fun updateProject(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: ResumeProjectDTO
	): ResponseEntity<ResumeProjectDTO> {
		val updated = resumeProjectService.updateForCurrentUser(dto.copy(id = id))
		return ResponseEntity.ok(updated)
	}

	@DeleteMapping("/projects/{id}")
	fun deleteProject(@PathVariable id: UUID): ResponseEntity<Unit> {
		resumeProjectService.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}

	@GetMapping("/certifications")
	fun getCertifications(): ResponseEntity<List<ResumeCertificationDTO>> =
		ResponseEntity.ok(resumeCertificationService.findAllForCurrentUser(
			org.springframework.data.domain.PageRequest.of(0, Int.MAX_VALUE)
		).content)

	@GetMapping("/certifications/{id}")
	fun getCertification(@PathVariable id: UUID): ResponseEntity<ResumeCertificationDTO> =
		ResponseEntity.ok(resumeCertificationService.getByIdForCurrentUser(id))

	@PostMapping("/certifications")
	fun createCertification(
		@Valid @RequestBody dto: ResumeCertificationDTO,
		request: HttpServletRequest
	): ResponseEntity<ResumeCertificationDTO> {
		val saved = resumeCertificationService.save(dto)
		return ResponseEntity.created(URI.create("${request.requestURI}/${saved.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/certifications/{id}")
	fun updateCertification(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: ResumeCertificationDTO
	): ResponseEntity<ResumeCertificationDTO> {
		val updated = resumeCertificationService.updateForCurrentUser(dto.copy(id = id))
		return ResponseEntity.ok(updated)
	}

	@DeleteMapping("/certifications/{id}")
	fun deleteCertification(@PathVariable id: UUID): ResponseEntity<Unit> {
		resumeCertificationService.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}

	@GetMapping("/languages")
	fun getLanguages(): ResponseEntity<List<ResumeLanguageDTO>> =
		ResponseEntity.ok(resumeLanguageService.findAllForCurrentUser(
			org.springframework.data.domain.PageRequest.of(0, Int.MAX_VALUE)
		).content)

	@GetMapping("/languages/{id}")
	fun getLanguage(@PathVariable id: UUID): ResponseEntity<ResumeLanguageDTO> =
		ResponseEntity.ok(resumeLanguageService.getByIdForCurrentUser(id))

	@PostMapping("/languages")
	fun createLanguage(
		@Valid @RequestBody dto: ResumeLanguageDTO,
		request: HttpServletRequest
	): ResponseEntity<ResumeLanguageDTO> {
		val saved = resumeLanguageService.save(dto)
		return ResponseEntity.created(URI.create("${request.requestURI}/${saved.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/languages/{id}")
	fun updateLanguage(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: ResumeLanguageDTO
	): ResponseEntity<ResumeLanguageDTO> {
		val updated = resumeLanguageService.updateForCurrentUser(dto.copy(id = id))
		return ResponseEntity.ok(updated)
	}

	@DeleteMapping("/languages/{id}")
	fun deleteLanguage(@PathVariable id: UUID): ResponseEntity<Unit> {
		resumeLanguageService.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}

	@GetMapping("/volunteer-work")
	fun getVolunteerWork(): ResponseEntity<List<VolunteerWorkDTO>> =
		ResponseEntity.ok(volunteerWorkService.findAllForCurrentUser(
			org.springframework.data.domain.PageRequest.of(0, Int.MAX_VALUE)
		).content)

	@GetMapping("/volunteer-work/{id}")
	fun getVolunteerWorkEntry(@PathVariable id: UUID): ResponseEntity<VolunteerWorkDTO> =
		ResponseEntity.ok(volunteerWorkService.getByIdForCurrentUser(id))

	@PostMapping("/volunteer-work")
	fun createVolunteerWork(
		@Valid @RequestBody dto: VolunteerWorkDTO,
		request: HttpServletRequest
	): ResponseEntity<VolunteerWorkDTO> {
		val saved = volunteerWorkService.save(dto)
		return ResponseEntity.created(URI.create("${request.requestURI}/${saved.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/volunteer-work/{id}")
	fun updateVolunteerWork(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: VolunteerWorkDTO
	): ResponseEntity<VolunteerWorkDTO> {
		val updated = volunteerWorkService.updateForCurrentUser(dto.copy(id = id))
		return ResponseEntity.ok(updated)
	}

	@DeleteMapping("/volunteer-work/{id}")
	fun deleteVolunteerWork(@PathVariable id: UUID): ResponseEntity<Unit> {
		volunteerWorkService.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}

	@GetMapping("/publications")
	fun getPublications(): ResponseEntity<List<ResumePublicationDTO>> =
		ResponseEntity.ok(resumePublicationService.findAllForCurrentUser(
			org.springframework.data.domain.PageRequest.of(0, Int.MAX_VALUE)
		).content)

	@GetMapping("/publications/{id}")
	fun getPublication(@PathVariable id: UUID): ResponseEntity<ResumePublicationDTO> =
		ResponseEntity.ok(resumePublicationService.getByIdForCurrentUser(id))

	@PostMapping("/publications")
	fun createPublication(
		@Valid @RequestBody dto: ResumePublicationDTO,
		request: HttpServletRequest
	): ResponseEntity<ResumePublicationDTO> {
		val saved = resumePublicationService.save(dto)
		return ResponseEntity.created(URI.create("${request.requestURI}/${saved.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/publications/{id}")
	fun updatePublication(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: ResumePublicationDTO
	): ResponseEntity<ResumePublicationDTO> {
		val updated = resumePublicationService.updateForCurrentUser(dto.copy(id = id))
		return ResponseEntity.ok(updated)
	}

	@DeleteMapping("/publications/{id}")
	fun deletePublication(@PathVariable id: UUID): ResponseEntity<Unit> {
		resumePublicationService.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}

	@GetMapping("/awards")
	fun getAwards(): ResponseEntity<List<ResumeAwardDTO>> =
		ResponseEntity.ok(resumeAwardService.findAllForCurrentUser(
			org.springframework.data.domain.PageRequest.of(0, Int.MAX_VALUE)
		).content)

	@GetMapping("/awards/{id}")
	fun getAward(@PathVariable id: UUID): ResponseEntity<ResumeAwardDTO> =
		ResponseEntity.ok(resumeAwardService.getByIdForCurrentUser(id))

	@PostMapping("/awards")
	fun createAward(
		@Valid @RequestBody dto: ResumeAwardDTO,
		request: HttpServletRequest
	): ResponseEntity<ResumeAwardDTO> {
		val saved = resumeAwardService.save(dto)
		return ResponseEntity.created(URI.create("${request.requestURI}/${saved.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/awards/{id}")
	fun updateAward(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: ResumeAwardDTO
	): ResponseEntity<ResumeAwardDTO> {
		val updated = resumeAwardService.updateForCurrentUser(dto.copy(id = id))
		return ResponseEntity.ok(updated)
	}

	@DeleteMapping("/awards/{id}")
	fun deleteAward(@PathVariable id: UUID): ResponseEntity<Unit> {
		resumeAwardService.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}

	@GetMapping("/affiliations")
	fun getAffiliations(): ResponseEntity<List<ProfessionalAffiliationDTO>> =
		ResponseEntity.ok(professionalAffiliationService.findAllForCurrentUser(
			org.springframework.data.domain.PageRequest.of(0, Int.MAX_VALUE)
		).content)

	@GetMapping("/affiliations/{id}")
	fun getAffiliation(@PathVariable id: UUID): ResponseEntity<ProfessionalAffiliationDTO> =
		ResponseEntity.ok(professionalAffiliationService.getByIdForCurrentUser(id))

	@PostMapping("/affiliations")
	fun createAffiliation(
		@Valid @RequestBody dto: ProfessionalAffiliationDTO,
		request: HttpServletRequest
	): ResponseEntity<ProfessionalAffiliationDTO> {
		val saved = professionalAffiliationService.save(dto)
		return ResponseEntity.created(URI.create("${request.requestURI}/${saved.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/affiliations/{id}")
	fun updateAffiliation(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: ProfessionalAffiliationDTO
	): ResponseEntity<ProfessionalAffiliationDTO> {
		val updated = professionalAffiliationService.updateForCurrentUser(dto.copy(id = id))
		return ResponseEntity.ok(updated)
	}

	@DeleteMapping("/affiliations/{id}")
	fun deleteAffiliation(@PathVariable id: UUID): ResponseEntity<Unit> {
		professionalAffiliationService.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}
}
