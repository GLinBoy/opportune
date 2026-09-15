package com.glinboy.opportune.config

import com.glinboy.opportune.security.webhook.WebhookAuthenticationEntryPoint
import com.glinboy.opportune.security.webhook.WebhookTokenAuthenticationFilter
import com.glinboy.opportune.service.WebhookTokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher

/**
 * Dedicated, higher-priority security chain for the webhook receive endpoint.
 *
 * It deliberately does not enable `oauth2ResourceServer`: session JWTs are therefore never decoded
 * on the webhook path and cannot be used against it. Conversely, the main chain keeps its JWT
 * resource server and will reject a webhook bearer token. The two credential types never mix.
 */
@Configuration
class WebhookSecurityConfiguration {

	@Bean
	@Order(1)
	fun webhookSecurityFilterChain(
		http: HttpSecurity,
		webhookTokenService: WebhookTokenService,
		webhookAuthenticationEntryPoint: WebhookAuthenticationEntryPoint
	): SecurityFilterChain {
		val webhookTokenAuthenticationFilter = WebhookTokenAuthenticationFilter(webhookTokenService)

		http
			.securityMatcher(PathPatternRequestMatcher.withDefaults().matcher(WEBHOOK_PATH_PATTERN))
			.cors(withDefaults())
			.csrf { csrf -> csrf.disable() }
			.sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.authorizeHttpRequests { authz -> authz.anyRequest().authenticated() }
			.addFilterBefore(webhookTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
			.exceptionHandling { exceptions ->
				exceptions.authenticationEntryPoint(webhookAuthenticationEntryPoint)
			}
		return http.build()
	}

	companion object {
		private const val WEBHOOK_PATH_PATTERN = "/api/webhook/**"
	}
}
