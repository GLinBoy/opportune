package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.BaseDTO
import com.glinboy.opportune.service.GenericService
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

abstract class GenericResource<ID, D: BaseDTO, S: GenericService<ID, D>>(
	protected val service: S
) {

	@GetMapping
	@PageableAsQueryParam
	fun getAll(
		@Parameter(hidden = true) pageable: Pageable,
		request: HttpServletRequest
	): ResponseEntity<List<D>> {
		val page: Page<D> = service.findAll(pageable)
		val headers: HttpHeaders =
			PaginationUtil.generatePaginationHttpHeaders(page, request.requestURI)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{id}")
	fun getById(@PathVariable id: ID): ResponseEntity<D> {
		val entity = service.getById(id)
		return ResponseEntity.ok().body(entity)
	}

	@PostMapping
	fun save(
		@Valid @RequestBody entity: D,
		request: HttpServletRequest
	): ResponseEntity<D> {
		val savedEntity = service.save(entity)
		val location = URI.create("${request.requestURI}/${savedEntity.id}")
		return ResponseEntity.created(location)
			.contentType(MediaType.APPLICATION_JSON)
			.body(savedEntity)
	}

	@PutMapping
	fun update(@Valid @RequestBody entity: D): ResponseEntity<D> {
		if (entity.id == null) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "ID must not be null"
			)
		}
		val updatedEntity = service.update(entity)
		return ResponseEntity.ok().body(updatedEntity)
	}

	@DeleteMapping("/{id}")
	fun deleteById(@PathVariable id: ID): ResponseEntity<Void> {
		service.delete(id)
		return ResponseEntity.noContent().build()
	}
}
