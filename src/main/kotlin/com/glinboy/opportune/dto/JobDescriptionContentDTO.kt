package com.glinboy.opportune.dto

/**
 * DTO representing the content extracted from a job posting URL
 */
data class JobDescriptionContentDTO(
	/**
	 * The raw HTML or text content fetched from the URL
	 */
	val content: String,

	/**
	 * The original URL that was fetched
	 */
	val url: String,

	/**
	 * The source type (e.g., "default", "linkedin", "indeed")
	 */
	val sourceType: String = "default",

	/**
	 * HTTP status code of the response
	 */
	val statusCode: Int,

	/**
	 * Content type from the response headers
	 */
	val contentType: String? = null,

	/**
	 * The page title extracted from the HTML <title> tag
	 */
	val title: String? = null
)

