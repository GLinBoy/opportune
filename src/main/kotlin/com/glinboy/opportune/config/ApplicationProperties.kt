package com.glinboy.opportune.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
class ApplicationProperties {
	var info: Info = Info()

	data class Info(
		var name: String? = null,
		var version: String? = null,
		var description: String? = null,
		var license: String? = null,
		var website: String? = null,
		var githubUrl: String? = null,
		var licenseUrl: String? = null,
		var servers: List<Server>? = null
	)

	data class Server(
		var url: String? = null,
		var description: String? = null
	)
}
