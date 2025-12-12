package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.BaseDTO
import com.glinboy.opportune.service.GenericService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.util.*

@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
abstract class GenericResource<ID, D : BaseDTO, S : GenericService<ID, D>>(
	protected val service: S
) {

	protected val log: Logger = LoggerFactory.getLogger(this::class.java)

	@GetMapping
	@PageableAsQueryParam
	open fun getAll(
		@Parameter(hidden = true) pageable: Pageable,
		@RequestParam(value = "query", required = false, defaultValue = "") query: String,
		request: HttpServletRequest
	): ResponseEntity<List<D>> {
		val specification = Optional.of<String>(query)
//			.filter { StringUtils.isNotBlank(it) }
//			.map { toSpecification<Any>(it) }
//			.orElseGet { Specification.allOf() }
		val page: Page<D> = service.findAllForCurrentUser(Specification.allOf(), pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{id}")
	open fun getById(@PathVariable id: ID): ResponseEntity<D> {
		val entity = service.getByIdForCurrentUser(id)
		return ResponseEntity.ok().body(entity)
	}

	@PostMapping
	open fun save(
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
	open fun update(@Valid @RequestBody entity: D): ResponseEntity<D> {
		if (entity.id == null) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST, "ID must not be null"
			)
		}
		val updatedEntity = service.updateForCurrentUser(entity)
		return ResponseEntity.ok().body(updatedEntity)
	}

	@DeleteMapping("/{id}")
	open fun deleteById(@PathVariable id: ID): ResponseEntity<Void> {
		service.deleteForCurrentUser(id)
		return ResponseEntity.noContent().build()
	}
}
