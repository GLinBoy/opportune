package com.glinboy.opportune.config

import com.glinboy.opportune.security.SecurityUtils
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {
	companion object {
		const val BEARER_AUTHENTICATION_NAME = "Bearer Authentication"
	}

	@Bean
	fun customizeOpenAPI(properties: ApplicationProperties): OpenAPI = OpenAPI()
		.info(
			Info()
				.title(properties.info.name)
				.description(properties.info.description)
				.version(properties.info.version)
				.license(
					License()
						.name(properties.info.license)
						.url(properties.info.licenseUrl)
				)
		)
		.externalDocs(
			ExternalDocumentation()
				.description("Website")
				.url(properties.info.website)
		)
		.servers(properties.info.servers?.map { Server().url(it.url).description(it.description) } )
		.components(
			Components()
				.addSecuritySchemes(
					BEARER_AUTHENTICATION_NAME,
					SecurityScheme()
						.name(BEARER_AUTHENTICATION_NAME)
						.type(SecurityScheme.Type.HTTP)
						.scheme(SecurityUtils.TYPE_TOKEN_BEARER)
						.bearerFormat("JWT")
				)
		)
}
