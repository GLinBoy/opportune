package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.JobDescriptionContentDTO
import com.glinboy.opportune.service.JobDescriptionParserStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * Parser strategy for Indeed job postings.
 * Currently delegates to default parser but can be enhanced in the future
 * to handle Indeed-specific features like API access or DOM parsing.
 */
@Component
@Order(2) // Higher priority than default, lower than LinkedIn
class IndeedJobDescriptionParserStrategy(
	private val defaultStrategy: DefaultJobDescriptionParserStrategy
) : JobDescriptionParserStrategy {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	companion object {
		private const val SOURCE_TYPE = "indeed"
		private val INDEED_PATTERNS = listOf(
			Regex("indeed\\.com/viewjob.*", RegexOption.IGNORE_CASE),
			Regex("indeed\\.com/.*/viewjob.*", RegexOption.IGNORE_CASE),
			Regex("indeed\\.com/.*rc/clk.*", RegexOption.IGNORE_CASE)
		)
	}

	override fun canHandle(url: String): Boolean {
		return INDEED_PATTERNS.any { it.containsMatchIn(url) }
	}

	override fun fetchContent(url: String): JobDescriptionContentDTO {
		log.debug("Fetching Indeed job posting from URL: {}", url)

		// TODO: Implement Indeed-specific parsing logic
		// For now, use the default strategy and update the source type
		// The title is already extracted by the default strategy
		val result = defaultStrategy.fetchContent(url)

		return result.copy(sourceType = SOURCE_TYPE)
	}

	override fun getSourceType(): String = SOURCE_TYPE
}

