package com.glinboy.opportune.web.rest

import com.glinboy.opportune.service.CompanyService
import com.glinboy.opportune.service.dto.CompanyDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/companies")
class CompanyResource(private val companyService: CompanyService) {

	@PostMapping
	fun createCompany(@RequestBody companyDTO: CompanyDTO): ResponseEntity<CompanyDTO> {
		val result = companyService.createCompany(companyDTO)
		return ResponseEntity.ok(result)
	}

	@GetMapping("/{id}")
	fun getCompany(@PathVariable id: Long): ResponseEntity<CompanyDTO> {
		val result = companyService.getCompany(id)
		return ResponseEntity.ok(result)
	}

	@PutMapping("/{id}")
	fun updateCompany(@PathVariable id: Long, @RequestBody companyDTO: CompanyDTO): ResponseEntity<CompanyDTO> {
		val result = companyService.updateCompany(id, companyDTO)
		return ResponseEntity.ok(result)
	}

	@DeleteMapping("/{id}")
	fun deleteCompany(@PathVariable id: Long): ResponseEntity<Void> {
		companyService.deleteCompany(id)
		return ResponseEntity.noContent().build()
	}
}
