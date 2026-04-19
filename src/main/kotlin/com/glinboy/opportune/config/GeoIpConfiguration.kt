package com.glinboy.opportune.config

import com.maxmind.db.CHMCache
import com.maxmind.geoip2.DatabaseReader
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader

/**
 * Exposes the MaxMind GeoLite2-City [DatabaseReader] as a Spring bean.
 *
 * The reader is loaded once at startup and shared by all threads.
 * It implements [Closeable], so Spring calls `close()` on it during context shutdown
 * via the `destroyMethod` attribute.
 *
 * The bean returns `null` when:
 * - `application.geoip.enabled = false`, or
 * - the configured MMDB file does not exist.
 *
 * In both cases a warning is logged and the [com.glinboy.opportune.service.GeoLocationService]
 * implementation gracefully skips the lookup.
 */
@Configuration
class GeoIpConfiguration(
	private val properties: ApplicationProperties,
	private val resourceLoader: ResourceLoader,
) {

	private val log = LoggerFactory.getLogger(this::class.java)

	@Bean(destroyMethod = "close")
	fun geoIpDatabaseReader(): DatabaseReader? {
		if (!properties.geoip.enabled) {
			log.warn("GeoIP lookup is disabled (application.geoip.enabled=false). " +
				"IP geolocation will not be available.")
			return null
		}

		val resource = resourceLoader.getResource(properties.geoip.dbPath)
		if (!resource.exists()) {
			log.warn(
				"GeoLite2 database file not found at '{}'. " +
					"IP geolocation will not be available. " +
					"Download GeoLite2-City.mmdb from https://dev.maxmind.com/geoip/geolite2-free-geolocation-data " +
					"and set application.geoip.db-path (or \$OPPORTUNE_GEOIP_DB_PATH) accordingly.",
				properties.geoip.dbPath
			)
			return null
		}

		return try {
			resource.inputStream.use { stream ->
				DatabaseReader.Builder(stream)
					.withCache(CHMCache())
					.build()
					.also { log.info("GeoLite2 database loaded from '{}'", properties.geoip.dbPath) }
			}
		} catch (e: Exception) {
			log.error("Failed to load GeoLite2 database from '{}': {}", properties.geoip.dbPath, e.message)
			null
		}
	}
}

