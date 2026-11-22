package com.glinboy.opportune

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.client.RestTestClient

/**
 * Verifies that the application starts with a real web server and that the health
 * endpoint reports the application as available.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class ApplicationSmokeTest {

	@Autowired
	private lateinit var restTestClient: RestTestClient

	@Test
	@DisplayName("Application starts and the health endpoint reports UP")
	fun `health endpoint reports UP`() {
		restTestClient.get()
			.uri("/management/health")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.jsonPath("$.status").isEqualTo("UP")
	}
}
