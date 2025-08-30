package com.glinboy.opportune.web.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class SpaWebFilter : OncePerRequestFilter() {

	/**
	 * Forwards any unmapped paths (except those containing a period) to the client
	 * `index.html`.
	 */
	@Throws(ServletException::class, IOException::class)
	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain
	) {
		// Request URI includes the contextPath if any, removed it.
		val path = request.requestURI.substring(request.contextPath.length)
		if (!path.startsWith("/api") &&
			!path.startsWith("/management") &&
			!path.startsWith("/v3/api-docs") &&
			!path.startsWith("/h2-console") &&
			!path.startsWith("/websocket") &&
			!path.contains(".") &&
			path.matches("/(.*)".toRegex())
		) {
			request.getRequestDispatcher("/index.html").forward(request, response)
			return
		}

		filterChain.doFilter(request, response)
	}
}
