package com.glinboy.opportune.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Profile("dev")
class CorsConfiguration : WebMvcConfigurer {

  override fun addCorsMappings(registry: CorsRegistry) {
    registry.addMapping("/**")
            .allowedOriginPatterns(
                    "http://localhost:8080",
                    "http://localhost:9090",
                    "chrome-extension://*"
            )
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
  }
}
