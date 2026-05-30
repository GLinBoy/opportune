package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.SystemSettingsDTO
import com.glinboy.opportune.service.AdminSettingsService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@Secured("ROLE_ADMIN")
@RequestMapping("/api/admin/settings")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class AdminSettingsResource(private val adminSettingsService: AdminSettingsService) {

    @GetMapping
    fun getSettings(): ResponseEntity<SystemSettingsDTO> =
        ResponseEntity.ok(adminSettingsService.getSettings())

    @PatchMapping
    fun updateSettings(@Valid @RequestBody dto: SystemSettingsDTO): ResponseEntity<SystemSettingsDTO> =
        ResponseEntity.ok(adminSettingsService.updateSettings(dto))
}
