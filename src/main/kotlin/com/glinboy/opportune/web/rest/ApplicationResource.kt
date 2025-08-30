package com.glinboy.opportune.web.rest

import com.glinboy.opportune.service.ApplicationService
import com.glinboy.opportune.service.dto.ApplicationDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/applications")
class ApplicationResource(private val applicationService: ApplicationService) {

	@PostMapping
	fun createApplication(@RequestBody applicationDTO: ApplicationDTO): ResponseEntity<ApplicationDTO> {
		val createdApplication = applicationService.createApplication(applicationDTO)
		return ResponseEntity(createdApplication, HttpStatus.CREATED)
	}

	@GetMapping("/{id}")
	fun getApplication(@PathVariable id: Long): ResponseEntity<ApplicationDTO> {
		val application = applicationService.getApplication(id)
		return ResponseEntity(application, HttpStatus.OK)
	}

	@PutMapping("/{id}")
	fun updateApplication(
		@PathVariable id: Long,
		@RequestBody applicationDTO: ApplicationDTO
	): ResponseEntity<ApplicationDTO> {
		val updatedApplication = applicationService.updateApplication(id, applicationDTO)
		return ResponseEntity(updatedApplication, HttpStatus.OK)
	}

	@DeleteMapping("/{id}")
	fun deleteApplication(@PathVariable id: Long): ResponseEntity<Unit> {
		applicationService.deleteApplication(id)
		return ResponseEntity(HttpStatus.NO_CONTENT)
	}
}
