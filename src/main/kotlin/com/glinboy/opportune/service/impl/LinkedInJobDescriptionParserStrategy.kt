package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.JobDescriptionContentDTO
import com.glinboy.opportune.service.JobDescriptionParserStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * Parser strategy for LinkedIn job postings.
 * Currently delegates to default parser but can be enhanced in the future
 * to handle LinkedIn-specific features like authentication, API access, or DOM parsing.
 */
@Component
@Order(1) // Higher priority than default
class LinkedInJobDescriptionParserStrategy(
	private val defaultStrategy: DefaultJobDescriptionParserStrategy
) : JobDescriptionParserStrategy {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	companion object {
		private const val SOURCE_TYPE = "linkedin"
		private val LINKEDIN_PATTERNS = listOf(
			Regex("linkedin\\.com/jobs/view/.*", RegexOption.IGNORE_CASE),
			Regex("linkedin\\.com/.*/jobs/.*", RegexOption.IGNORE_CASE)
		)
	}

	override fun canHandle(url: String): Boolean {
		return LINKEDIN_PATTERNS.any { it.containsMatchIn(url) }
	}

	override fun fetchContent(url: String): JobDescriptionContentDTO {
		log.debug("Fetching LinkedIn job posting from URL: {}", url)

		// TODO: Implement LinkedIn-specific parsing logic
		// For now, use the default strategy and update the source type
		// The title is already extracted by the default strategy
		val result = defaultStrategy.fetchContent(url)

		return result.copy(sourceType = SOURCE_TYPE)
	}

	override fun getSourceType(): String = SOURCE_TYPE
}

