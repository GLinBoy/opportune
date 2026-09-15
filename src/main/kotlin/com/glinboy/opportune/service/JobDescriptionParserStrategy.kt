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
	 * Parse already-fetched HTML content into a job description without
	 * performing any network call.
	 *
	 * @param url The URL the HTML was captured from
	 * @param html The already-fetched HTML body
	 * @return JobDescriptionContentDTO containing the parsed content
	 */
	fun parseContent(url: String, html: String): JobDescriptionContentDTO

	/**
	 * Get the source type identifier for this strategy
	 *
	 * @return The source type (e.g., "linkedin", "indeed", "default")
	 */
	fun getSourceType(): String
}

