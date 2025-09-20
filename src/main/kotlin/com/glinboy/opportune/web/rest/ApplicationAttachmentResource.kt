package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationAttachmentDTO
import com.glinboy.opportune.service.ApplicationAttachmentService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
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

// FIXME this should handle multiple attachments per application
@RestController
@RequestMapping("/api/applications")
class ApplicationAttachmentResource(private val applicationAttachmentService: ApplicationAttachmentService) {

	@PageableAsQueryParam
	@GetMapping("/{application_id}/attachments")
	fun getInterviewAttachments(
		@PathVariable("application_id") applicationId: UUID,
		@Parameter(hidden = true) pageable: Pageable,
		request: HttpServletRequest
	): ResponseEntity<List<ApplicationAttachmentDTO>> {
		val page = applicationAttachmentService.findByApplicationId(applicationId, pageable)
		val headers: HttpHeaders =
			PaginationUtil.generatePaginationHttpHeaders(page, request.requestURI)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{application_id}/attachments/{id}")
	fun getInterviewAttachment(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<ApplicationAttachmentDTO> =
		applicationAttachmentService.findByApplicationIdAndId(applicationId, id)
			.map { ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(it) }
			.orElse(ResponseEntity.notFound().build())

	@DeleteMapping("/{application_id}/attachments/{id}")
	fun deleteInterviewAttachment(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable(name = "id") id: UUID
	): ResponseEntity<Void> {
		applicationAttachmentService.deleteByApplicationIdAndId(applicationId, id)
		return ResponseEntity.noContent().build<Void>()
	}

	@PostMapping("/{application_id}/attachments", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun uploadInterviewAttachment(
		@PathVariable("application_id") applicationId: UUID,
		@PathVariable("note_id") noteId: UUID,
		@RequestParam("interview_attachment") interviewAttachment: MultipartFile,
		request: HttpServletRequest
	): ResponseEntity<ApplicationAttachmentDTO> {
		// TODO implement file upload (FileService?)
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
	}
}
