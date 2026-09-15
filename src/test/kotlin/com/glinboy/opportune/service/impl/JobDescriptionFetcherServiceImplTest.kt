package com.glinboy.opportune.service.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class JobDescriptionFetcherServiceImplTest {

	private val html = """
		<html>
			<head><title>  Senior   Kotlin Engineer </title></head>
			<body><p>Build great things.</p></body>
		</html>
	""".trimIndent()

	private fun service(): JobDescriptionFetcherServiceImpl {
		val defaultStrategy = DefaultJobDescriptionParserStrategy()
		return JobDescriptionFetcherServiceImpl(
			listOf(
				LinkedInJobDescriptionParserStrategy(defaultStrategy),
				IndeedJobDescriptionParserStrategy(defaultStrategy),
				defaultStrategy
			)
		)
	}

	@Test
	fun `parseContent extracts title and keeps captured content for default source`() {
		val result = service().parseContent("https://example.com/jobs/1", html)

		assertEquals(html, result.content)
		assertEquals("Senior Kotlin Engineer", result.title)
		assertEquals("default", result.sourceType)
		assertEquals("https://example.com/jobs/1", result.url)
	}

	@Test
	fun `parseContent tags LinkedIn source type`() {
		val result = service().parseContent("https://www.linkedin.com/jobs/view/123", html)

		assertEquals("linkedin", result.sourceType)
		assertEquals("Senior Kotlin Engineer", result.title)
		assertEquals(html, result.content)
	}

	@Test
	fun `parseContent tags Indeed source type`() {
		val result = service().parseContent("https://www.indeed.com/viewjob?jk=123", html)

		assertEquals("indeed", result.sourceType)
		assertEquals("Senior Kotlin Engineer", result.title)
		assertEquals(html, result.content)
	}

	@Test
	fun `parseContent returns null title when html has no title tag`() {
		val result = service().parseContent("https://example.com/jobs/2", "<html><body>No title</body></html>")

		assertNull(result.title)
	}

	@Test
	fun `parseContent rejects blank url`() {
		assertThrows(IllegalArgumentException::class.java) {
			service().parseContent("  ", html)
		}
	}

	@Test
	fun `parseContent rejects blank html`() {
		assertThrows(IllegalArgumentException::class.java) {
			service().parseContent("https://example.com/jobs/1", "  ")
		}
	}
}
