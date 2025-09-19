package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationResumeDTO
import com.glinboy.opportune.service.ApplicationResumeService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/applications")
class ApplicationResumeResource(private val applicationResumeService: ApplicationResumeService) {

	@GetMapping("/{application_id}/resumes", "/{application_id}/resumes/{id}")
	fun getApplicationResume(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable(name = "id") optionalId: Optional<UUID>
	): ResponseEntity<ApplicationResumeDTO> =
		optionalId.map { applicationResumeService.findById(it) }
			.orElseGet { applicationResumeService.findByApplicationId(applicationId) }
			.map { ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(it) }
			.orElse(ResponseEntity.notFound().build())

	@DeleteMapping("/{application_id}/resumes", "/{application_id}/resumes/{id}")
	fun deleteApplicationResume(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable(name = "id") optionalId: Optional<UUID>
	): ResponseEntity<Void> = optionalId.map { applicationResumeService.delete(it) }
		.orElseGet { applicationResumeService.deleteByApplicationId(applicationId) }
		.let { ResponseEntity.noContent().build<Void>() }

	@PostMapping("/{application_id}/resumes", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun uploadApplicationResume(
		@PathVariable("application_id") applicationId: UUID,
		@RequestParam("application_resume") applicationResume: MultipartFile,
		request: HttpServletRequest
	): ResponseEntity<ApplicationResumeDTO> {
		// TODO implement file upload (FileService?)
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
	}
}
