package com.glinboy.opportune.service

import com.glinboy.opportune.dto.JobDescriptionContentDTO

/**
 * Service interface for fetching and parsing job descriptions from URLs
 */
interface JobDescriptionFetcherService {

	/**
	 * Fetch and parse job description content from the given URL.
	 * The service will automatically determine the appropriate parser strategy
	 * based on the URL (LinkedIn, Indeed, or default).
	 *
	 * @param url The URL to fetch content from
	 * @return JobDescriptionContentDTO containing the fetched content
	 * @throws IllegalArgumentException if the URL is invalid
	 * @throws Exception if fetching or parsing fails
	 */
	fun fetchJobDescription(url: String): JobDescriptionContentDTO
}

