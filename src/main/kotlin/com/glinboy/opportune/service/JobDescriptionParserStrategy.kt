package com.glinboy.opportune.service

import com.glinboy.opportune.dto.JobDescriptionContentDTO

/**
 * Strategy interface for parsing job descriptions from different sources
 */
interface JobDescriptionParserStrategy {

	/**
	 * Check if this strategy can handle the given URL
	 *
	 * @param url The URL to check
	 * @return true if this strategy can handle the URL, false otherwise
	 */
	fun canHandle(url: String): Boolean

	/**
	 * Fetch and parse the job description content from the given URL
	 *
	 * @param url The URL to fetch content from
	 * @return JobDescriptionContentDTO containing the fetched content
	 * @throws Exception if fetching or parsing fails
	 */
	fun fetchContent(url: String): JobDescriptionContentDTO

	/**
	 * Get the source type identifier for this strategy
	 *
	 * @return The source type (e.g., "linkedin", "indeed", "default")
	 */
	fun getSourceType(): String
}

