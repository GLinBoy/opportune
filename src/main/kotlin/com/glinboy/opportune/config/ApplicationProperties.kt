package com.glinboy.opportune.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
class ApplicationProperties {
	var config: Config = Config()
	var info: Info = Info()
	var mail: Mail = Mail()
	var security: Security = Security()
	var files: Files = Files()
	var geoip: GeoIp = GeoIp()
	var web: Web = Web()

	data class Config(
		var dashboard: Dashboard = Dashboard(),
	)

	data class Dashboard(
		var summaryDays: Long = 90,
		var trendDays: Long = 30,
		var aiQueuePreviewSize: Int = 10
	)

	data class Web(
		var forwardedForHeader: String = "X-Forwarded-For"
	)

	data class GeoIp(
		var dbPath: String = "classpath:GeoLite2-City.mmdb",
		var enabled: Boolean = true
	)

	data class Files(var basePath: String = "./uploads")

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

	data class Mail(
		var from: String? = null,
	)

	data class Server(var url: String? = null, var description: String? = null)

	data class Security(
		var contentSecurityPolicy: String? = null,
		var validationCodeDuration: ValidationCodeDuration = ValidationCodeDuration(),
		var authentication: Authentication = Authentication()
	)

	data class ValidationCodeDuration(
		var emailVerify: Duration? = null,
		var passwordReset: Duration? = null
	)

	data class Authentication(var jwt: Jwt = Jwt())

	data class Jwt(
		var base64Secret: String? = null,
		var tokenValidityInSeconds: Long? = null,
		var tokenValidityInSecondsForRememberMe: Long? = null,
		var refreshTokenValidityInSeconds: Long? = null
	)
}
