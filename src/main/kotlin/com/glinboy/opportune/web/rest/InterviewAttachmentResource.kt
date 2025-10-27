package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.InterviewAttachmentDTO
import com.glinboy.opportune.service.InterviewAttachmentService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/applications")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class InterviewAttachmentResource(private val interviewAttachmentService: InterviewAttachmentService) {

	@PageableAsQueryParam
	@GetMapping("/{application_id}/interview-notes/{interview_note_id}/attachments")
	fun getInterviewAttachments(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable("interview_note_id") interviewNoteId: UUID,
		@Parameter(hidden = true) pageable: Pageable,
		request: HttpServletRequest
	): ResponseEntity<List<InterviewAttachmentDTO>> {
		val page = interviewAttachmentService
			.findByApplicationIdAndInterviewNoteIdForCurrentUser(applicationId, interviewNoteId, pageable)
		val headers: HttpHeaders =
			PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{application_id}/interview-notes/{interview_note_id}/attachments/{id}")
	fun getInterviewAttachment(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable("interview_note_id") interviewNoteId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<InterviewAttachmentDTO> =
		interviewAttachmentService
			.findByApplicationIdANdInterviewNoteIdAndIdForCurrentUser(applicationId, interviewNoteId, id)
			.map { ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(it) }
			.orElse(ResponseEntity.notFound().build())

	@DeleteMapping("/{application_id}/interview-notes/{interview_note_id}/attachments/{id}")
	fun deleteInterviewAttachment(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable("interview_note_id") interviewNoteId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<Void> {
		interviewAttachmentService
			.deleteByApplicationIdAndInterviewNoteIdAndIdForCurrentUser(applicationId, interviewNoteId, id)
		return ResponseEntity.noContent().build<Void>()
	}

	@PostMapping("/{application_id}/interview-notes/{interview_note_id}/attachments", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun uploadInterviewAttachment(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable("interview_note_id") noteId: UUID,
		@RequestParam("interview_attachment") interviewAttachment: MultipartFile,
		request: HttpServletRequest
	): ResponseEntity<InterviewAttachmentDTO> {
		// TODO implement file upload (FileService?)
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
	}
}
