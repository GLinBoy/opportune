package com.glinboy.opportune.service.impl

import com.glinboy.opportune.config.ApplicationProperties
import com.glinboy.opportune.dto.ApplicationUrlSubmissionDTO
import com.glinboy.opportune.dto.JobDescriptionContentDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.enums.ApplicationStatus
import com.glinboy.opportune.event.ApplicationSubmittedEvent
import com.glinboy.opportune.mapper.ApplicationDetailsMapper
import com.glinboy.opportune.mapper.ApplicationMapper
import com.glinboy.opportune.repository.ApplicationRepository
import com.glinboy.opportune.service.JobDescriptionFetcherService
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.server.ResponseStatusException
import java.util.*

@ExtendWith(MockitoExtension::class)
class ApplicationWebhookCreationTest {

	@Mock
	private lateinit var repository: ApplicationRepository

	@Mock
	private lateinit var applicationDetailsMapper: ApplicationDetailsMapper

	@Mock
	private lateinit var entityManager: EntityManager

	@Mock
	private lateinit var jobDescriptionFetcherService: JobDescriptionFetcherService

	@Mock
	private lateinit var eventPublisher: ApplicationEventPublisher

	private val profileId: UUID = UUID.randomUUID()
	private lateinit var service: ApplicationServiceImpl

	private val capturedHtml = """
		<html>
			<head><title>  Senior   Kotlin Engineer </title></head>
			<body><p>Build great things.</p></body>
		</html>
	""".trimIndent()

	@BeforeEach
	fun setUp() {
		service = ApplicationServiceImpl(
			repository,
			ApplicationMapper(),
			applicationDetailsMapper,
			entityManager,
			jobDescriptionFetcherService,
			eventPublisher,
			ApplicationProperties()
		)
		SecurityContextHolder.getContext().authentication =
			UsernamePasswordAuthenticationToken(profileId.toString(), null, emptyList())
	}

	@AfterEach
	fun tearDown() {
		SecurityContextHolder.clearContext()
	}

	private fun stubPersist() {
		`when`(repository.save(any(Application::class.java))).thenAnswer { invocation ->
			(invocation.arguments[0] as Application).copy(id = UUID.randomUUID())
		}
	}

	private fun stubNoDuplicate() {
		`when`(repository.findOne(any<Specification<Application>>())).thenReturn(Optional.empty())
	}

	private fun content(title: String, body: String) = JobDescriptionContentDTO(
		content = body,
		url = "https://example.com/jobs/1",
		sourceType = "default",
		statusCode = 200,
		title = title
	)

	@Test
	fun `createApplicationFromPage parses captured html and saves an initiated application`() {
		val url = "https://example.com/jobs/1"
		stubNoDuplicate()
		stubPersist()
		`when`(jobDescriptionFetcherService.parseContent(url, capturedHtml))
			.thenReturn(content("Senior Kotlin Engineer", capturedHtml))

		val saved = service.createApplicationFromPage(profileId, url, capturedHtml)

		assertEquals(ApplicationStatus.INITIATED, saved.status)
		assertEquals("Senior Kotlin Engineer", saved.title)
		assertEquals(capturedHtml, saved.rawContent)
		assertEquals(profileId, saved.profileId)
		verify(jobDescriptionFetcherService).parseContent(url, capturedHtml)
		verify(jobDescriptionFetcherService, never()).fetchJobDescription(anyString())
		verify(eventPublisher).publishEvent(any(ApplicationSubmittedEvent::class.java))
	}

	@Test
	fun `createApplicationFromPage falls back to server fetch when captured html cannot be parsed`() {
		val url = "https://example.com/jobs/2"
		stubNoDuplicate()
		stubPersist()
		`when`(jobDescriptionFetcherService.parseContent(url, capturedHtml))
			.thenThrow(IllegalArgumentException("HTML content cannot be blank"))
		`when`(jobDescriptionFetcherService.fetchJobDescription(url))
			.thenReturn(content("Fetched Title", "<html>fetched</html>"))

		val saved = service.createApplicationFromPage(profileId, url, capturedHtml)

		assertEquals("Fetched Title", saved.title)
		assertEquals("<html>fetched</html>", saved.rawContent)
		verify(jobDescriptionFetcherService).parseContent(url, capturedHtml)
		verify(jobDescriptionFetcherService).fetchJobDescription(url)
	}

	@Test
	fun `createApplicationFromPage fetches the url when html is absent`() {
		val url = "https://example.com/jobs/3"
		stubNoDuplicate()
		stubPersist()
		`when`(jobDescriptionFetcherService.fetchJobDescription(url))
			.thenReturn(content("Fetched Title", "<html>fetched</html>"))

		val saved = service.createApplicationFromPage(profileId, url, null)

		assertEquals(ApplicationStatus.INITIATED, saved.status)
		verify(jobDescriptionFetcherService, never()).parseContent(anyString(), anyString())
		verify(jobDescriptionFetcherService).fetchJobDescription(url)
	}

	@Test
	fun `createApplicationFromPage rejects a duplicate profile url with 409`() {
		val url = "https://example.com/jobs/4"
		`when`(jobDescriptionFetcherService.parseContent(url, capturedHtml))
			.thenReturn(content("Senior Kotlin Engineer", capturedHtml))
		`when`(repository.findOne(any<Specification<Application>>()))
			.thenReturn(Optional.of(Application(id = UUID.randomUUID(), url = url)))

		val exception = assertThrows(ResponseStatusException::class.java) {
			service.createApplicationFromPage(profileId, url, capturedHtml)
		}

		assertEquals(HttpStatus.CONFLICT, exception.statusCode)
		assertTrue(exception.reason?.contains("already exists") == true)
		verify(repository, never()).save(any(Application::class.java))
	}

	@Test
	fun `submitApplicationUrl keeps fetching the url and saving an initiated application`() {
		val url = "https://example.com/jobs/5"
		stubNoDuplicate()
		stubPersist()
		`when`(jobDescriptionFetcherService.fetchJobDescription(url))
			.thenReturn(content("Manual Title", "<html>manual</html>"))

		val result = service.submitApplicationUrl(ApplicationUrlSubmissionDTO(url))

		assertTrue(result.isPresent)
		assertEquals(ApplicationStatus.INITIATED, result.get().status)
		assertEquals("<html>manual</html>", result.get().rawContent)
		verify(jobDescriptionFetcherService, never()).parseContent(anyString(), anyString())
		verify(eventPublisher).publishEvent(any(ApplicationSubmittedEvent::class.java))
	}
}
