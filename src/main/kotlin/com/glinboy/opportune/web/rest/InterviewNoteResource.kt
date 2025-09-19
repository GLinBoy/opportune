package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.InterviewNoteDTO
import com.glinboy.opportune.service.InterviewNoteService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/api/applications")
class InterviewNoteResource(private val interviewNoteService: InterviewNoteService) {

	@PageableAsQueryParam
	@GetMapping("/{application_id}/interview-notes")
	fun getInterviewNotes(
		@PathVariable("application_id") applicationId: UUID,
		@Parameter(hidden = true) pageable: Pageable,
		request: HttpServletRequest
	): ResponseEntity<List<InterviewNoteDTO>> {
		val page: Page<InterviewNoteDTO> = interviewNoteService.findAll(applicationId, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request.requestURI)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{application_id}/interview-notes/{id}")
	fun getInterviewNote(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<InterviewNoteDTO> = interviewNoteService.findById(applicationId, id)
		.map { ResponseEntity.ok().body(it) }
		.orElse(ResponseEntity.notFound().build())

	@PostMapping("/{application_id}/interview-notes")
	fun saveInterviewNote(
		@PathVariable("application_id") applicationId: UUID,
		@Valid @RequestBody interviewNoteDTO: InterviewNoteDTO,
		request: HttpServletRequest
	): ResponseEntity<InterviewNoteDTO> {
		if (applicationId != interviewNoteDTO.applicationId) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Application ID in path must match application ID in body"
			)
		}
		val saved = interviewNoteService.save(interviewNoteDTO)
		val location = URI.create("${request.requestURI}/${saved.id}")
		return ResponseEntity.created(location)
			.contentType(MediaType.APPLICATION_JSON)
			.body(saved)
	}

	@PutMapping("/{application_id}/interview-notes")
	fun updateInterviewNote(
		@PathVariable("application_id") applicationId: UUID,
		@Valid @RequestBody interviewNoteDTO: InterviewNoteDTO
	): ResponseEntity<InterviewNoteDTO> {
		if (interviewNoteDTO.id == null) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "ID must not be null"
			)
		}
		if (applicationId != interviewNoteDTO.applicationId) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Application ID in path must match application ID in body"
			)
		}
		return ResponseEntity.ok().body(interviewNoteService.update(interviewNoteDTO))
	}

	@DeleteMapping("/{application_id}/interview-notes/{id}")
	fun deleteInterviewNote(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<Unit> {
		interviewNoteService.delete(applicationId, id)
		return ResponseEntity.noContent().build()
	}
}
