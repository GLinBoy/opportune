package com.glinboy.opportune.config

import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.security.jwt.JwtAuthenticationConverter
import com.glinboy.opportune.service.ProfileService
import com.glinboy.opportune.web.filter.SpaWebFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher

@Configuration
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfiguration(
	private val env: Environment,
	private val properties: ApplicationProperties
) {

	private val resourcePaths = arrayOf(
		"/",
		"/index.html",
		"/favicon.ico",
		"/assets/**",
		"/error",
		"/*.css",
		"/*.js",
		"/*.png",
		"/*.jpg",
		"/*.jpeg",
		"/*.gif",
		"/*.svg",
		"/*.woff2",
		"/*.woff",
		"/*.ttf",
		"/swagger-ui.html",
		"/v3/api-docs/**",
		"/swagger-ui/**",
		"/h2-console/**",
		"/management/health",
		"/management/health/**",
		"/management/info",
		"/management/prometheus"
	)

	@Bean
	fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

	@Bean
	fun filterChain(
		http: HttpSecurity,
		profileService: ProfileService,
		jwtAuthenticationConverter: JwtAuthenticationConverter
	): SecurityFilterChain {
		http
			.cors(withDefaults())
			.csrf { csrf -> csrf.disable() }
			.addFilterAfter(SpaWebFilter(), BasicAuthenticationFilter::class.java)
			.headers { headers ->
				headers
					.contentSecurityPolicy { csp -> csp.policyDirectives(properties.security.contentSecurityPolicy) }
					.frameOptions { frameOptions ->
						if (env.acceptsProfiles(Profiles.of("dev"))) {
							frameOptions.sameOrigin()
						} else {
							frameOptions.deny()
						}
					}
					.referrerPolicy { referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN) }
					.permissionsPolicyHeader { permissions ->
						permissions.policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
					}
			}
			.authorizeHttpRequests { authz ->
				authz
					.requestMatchers(
						*resourcePaths
							.map { PathPatternRequestMatcher.withDefaults().matcher(it) }
							.toTypedArray()
					).permitAll()
				if (env.acceptsProfiles(Profiles.of("dev"))) {
					authz.requestMatchers("/h2-console/**").permitAll()
				}
				authz
					.requestMatchers(HttpMethod.POST, "/api/register").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
					.requestMatchers(HttpMethod.PUT, "/api/auth/password/reset/finish").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/auth/password/reset/init").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/auth/token/refresh").authenticated()
					.requestMatchers(HttpMethod.PUT, "/api/profiles/email/confirm").permitAll()
					.requestMatchers("/api/admin/**").hasAuthority(Role.ROLE_ADMIN.name)
					.requestMatchers("/api/**").hasAuthority(Role.ROLE_USER.name)
					.anyRequest().authenticated()
			}
			.sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.userDetailsService(profileService)
			.exceptionHandling { exceptions ->
				exceptions
					.authenticationEntryPoint(BearerTokenAuthenticationEntryPoint())
					.accessDeniedHandler(BearerTokenAccessDeniedHandler())
			}
			.oauth2ResourceServer { oauth2 ->
				oauth2.jwt { jwt ->
					jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
				}
			}
		return http.build()
	}
}
