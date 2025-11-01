package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.JobDescriptionContentDTO
import com.glinboy.opportune.service.JobDescriptionParserStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * Default implementation for fetching job description content from any URL.
 * This strategy is used as a fallback when no specific parser is available.
 */
@Component
@Order(Int.MAX_VALUE) // Lowest priority - used as fallback
class DefaultJobDescriptionParserStrategy : JobDescriptionParserStrategy {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	private val httpClient: HttpClient = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(10))
		.followRedirects(HttpClient.Redirect.NORMAL)
		.build()

	companion object {
		private const val SOURCE_TYPE = "default"
		private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
	}

	override fun canHandle(url: String): Boolean {
		// Default strategy can handle any valid URL
		return try {
			URI.create(url)
			true
		} catch (e: Exception) {
			log.warn("Invalid URL format: {}", url, e)
			false
		}
	}

	override fun fetchContent(url: String): JobDescriptionContentDTO {
		log.debug("Fetching content from URL using default strategy: {}", url)

		try {
			val request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("User-Agent", USER_AGENT)
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.header("Accept-Language", "en-US,en;q=0.9")
				.GET()
				.timeout(Duration.ofSeconds(30))
				.build()

			val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

			log.debug("Received response with status code: {}", response.statusCode())

			if (response.statusCode() !in 200..299) {
				log.warn("Non-successful status code {} for URL: {}", response.statusCode(), url)
			}

			val responseBody = response.body()
			val pageTitle = extractTitle(responseBody)

			return JobDescriptionContentDTO(
				content = responseBody,
				url = url,
				sourceType = SOURCE_TYPE,
				statusCode = response.statusCode(),
				contentType = response.headers().firstValue("Content-Type").orElse(null),
				title = pageTitle
			)
		} catch (e: Exception) {
			log.error("Error fetching content from URL: {}", url, e)
			throw RuntimeException("Failed to fetch content from URL: ${e.message}", e)
		}
	}

	override fun getSourceType(): String = SOURCE_TYPE

	/**
	 * Extract the title from HTML content using regex.
	 * This is a simple implementation that looks for the <title> tag.
	 *
	 * @param html The HTML content
	 * @return The extracted title or null if not found
	 */
	private fun extractTitle(html: String): String? {
		return try {
			// Look for <title>...</title> tag (case insensitive)
			val titleRegex = Regex("<title[^>]*>(.*?)</title>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
			val matchResult = titleRegex.find(html)

			matchResult?.groupValues?.getOrNull(1)
				?.trim()
				?.replace(Regex("\\s+"), " ") // Normalize whitespace
				?.let { if (it.isNotBlank()) it else null }
		} catch (e: Exception) {
			log.warn("Failed to extract title from HTML", e)
			null
		}
	}
}

