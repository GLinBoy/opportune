package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.JobDescriptionContentDTO
import com.glinboy.opportune.service.JobDescriptionFetcherService
import com.glinboy.opportune.service.JobDescriptionParserStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service implementation for fetching and parsing job descriptions from various sources.
 * This service uses a strategy pattern to support multiple job posting sources
 * (LinkedIn, Indeed, and a default fallback).
 */
@Service
class JobDescriptionFetcherServiceImpl(
	private val parserStrategies: List<JobDescriptionParserStrategy>
) : JobDescriptionFetcherService {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	init {
		log.info("Initialized JobDescriptionFetcherService with {} parser strategies", parserStrategies.size)
		parserStrategies.forEach { strategy ->
			log.debug("Registered parser strategy: {}", strategy.getSourceType())
		}
	}

	override fun fetchJobDescription(url: String): JobDescriptionContentDTO {
		require(url.isNotBlank()) { "URL cannot be blank" }

		log.info("Fetching job description from URL: {}", url)

		// Find the first strategy that can handle this URL
		val strategy = parserStrategies.firstOrNull { it.canHandle(url) }
			?: throw IllegalStateException("No parser strategy found that can handle URL: $url")

		log.debug("Selected parser strategy: {} for URL: {}", strategy.getSourceType(), url)

		return try {
			val result = strategy.fetchContent(url)
			log.info("Successfully fetched job description from {} (source: {})", url, result.sourceType)
			result
		} catch (e: Exception) {
			log.error("Failed to fetch job description from URL: {}", url, e)
			throw e
		}
	}
}

