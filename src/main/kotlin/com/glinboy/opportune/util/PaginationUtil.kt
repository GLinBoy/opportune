package com.glinboy.opportune.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Utility class for handling pagination.
 *
 * Pagination uses the same principles as the
 * [GitHub API](https://developer.github.com/v3/#pagination), and
 * follows [RFC 5988 (Link header)](http://tools.ietf.org/html/rfc5988).
 */
object PaginationUtil {

	fun <T> generatePaginationHttpHeaders(page: Page<T>, request: HttpServletRequest): HttpHeaders {
		val headers = HttpHeaders()
		headers.add("X-Total-Count", page.totalElements.toString())

		val baseUrl = request.requestURI
		val queryParams = request.queryString?.split("&")
			?.filter { !it.startsWith("page=") && !it.startsWith("size=") }
			?.joinToString("&") ?: ""

		var link = ""
		if ((page.number + 1) < page.totalPages) {
			link = "<${generateUri(baseUrl, page.number + 1, page.size, queryParams)}>; rel=\"next\","
		}
		// prev link
		if (page.number > 0) {
			link += "<${generateUri(baseUrl, page.number - 1, page.size, queryParams)}>; rel=\"prev\","
		}
		// last and first link
		val lastPage = if (page.totalPages > 0) page.totalPages - 1 else 0
		link += "<${generateUri(baseUrl, lastPage, page.size, queryParams)}>; rel=\"last\","
		link += "<${generateUri(baseUrl, 0, page.size, queryParams)}>; rel=\"first\""

		headers.add(HttpHeaders.LINK, link)
		return headers
	}

	private fun generateUri(baseUrl: String, page: Int, size: Int, additionalParams: String = ""): String {
		val builder = UriComponentsBuilder.fromUriString(baseUrl)
			.queryParam("page", page)
			.queryParam("size", size)

		if (additionalParams.isNotEmpty()) {
			return "${builder.toUriString()}&$additionalParams"
		}
		return builder.toUriString()
	}

	fun <T> generateSearchPaginationHttpHeaders(query: String, page: Page<T>, baseUrl: String): HttpHeaders {
		val escapedQuery = try {
			URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
		} catch (e: Exception) {
			throw RuntimeException(e)
		}

		val headers = HttpHeaders()
		headers.add("X-Total-Count", page.totalElements.toString())

		var link = ""
		if ((page.number + 1) < page.totalPages) {
			link =
				"<${generateUri(baseUrl, page.number + 1, page.size)}&query=$escapedQuery>; rel=\"next\","
		}
		// prev link
		if (page.number > 0) {
			link += "<${generateUri(baseUrl, page.number - 1, page.size)}&query=$escapedQuery>; rel=\"prev\","
		}
		// last and first link
		val lastPage = if (page.totalPages > 0) page.totalPages - 1 else 0
		link += "<${generateUri(baseUrl, lastPage, page.size)}&query=$escapedQuery>; rel=\"last\","
		link += "<${generateUri(baseUrl, 0, page.size)}&query=$escapedQuery>; rel=\"first\""

		headers.add(HttpHeaders.LINK, link)
		return headers
	}
}
