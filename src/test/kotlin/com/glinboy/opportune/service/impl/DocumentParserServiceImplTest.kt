package com.glinboy.opportune.service.impl

import com.glinboy.opportune.service.ResumeTextExtractor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*

class DocumentParserServiceImplTest {

    @Test
    fun `parseResumeData injects resumeText variable and returns parsed result`() {
        val mockJson = """
            {
                "work_experiences": [
                    {
                        "job_title": "Software Engineer",
                        "company": "TechCorp",
                        "location": "San Francisco, CA",
                        "start_month": 3,
                        "start_year": 2020,
                        "end_month": 6,
                        "end_year": 2023,
                        "is_current": false,
                        "display_order": 0,
                        "bullets": [
                            {"content": "Built REST APIs", "display_order": 0},
                            {"content": "Led code reviews", "display_order": 1}
                        ]
                    },
                    {
                        "job_title": "Junior Developer",
                        "company": "StartupXYZ",
                        "location": "Remote",
                        "start_month": 1,
                        "start_year": 2018,
                        "end_month": 2,
                        "end_year": 2020,
                        "is_current": false,
                        "display_order": 1,
                        "bullets": [
                            {"content": "Developed frontend components", "display_order": 0}
                        ]
                    }
                ],
                "education": [
                    {
                        "school": "University of Technology",
                        "degree": "Bachelor of Science",
                        "field_of_study": "Computer Science",
                        "start_year": 2014,
                        "end_year": 2018,
                        "is_current": false,
                        "gpa": "3.8",
                        "honors": "Cum Laude",
                        "display_order": 0,
                        "courses": ["Data Structures", "Algorithms", "Machine Learning"]
                    }
                ],
                "skill_groups": [
                    {
                        "category": "Programming Languages",
                        "display_order": 0,
                        "skills": ["Kotlin", "Java", "TypeScript"]
                    },
                    {
                        "category": "Tools",
                        "display_order": 1,
                        "skills": ["Git", "Docker", "AWS"]
                    }
                ]
            }
        """.trimIndent()

        val pdfExtractor = mock(ResumeTextExtractor::class.java)
        val chatClientBuilder = mock(org.springframework.ai.chat.client.ChatClient.Builder::class.java)
        val chatClient = mock(org.springframework.ai.chat.client.ChatClient::class.java)
        val requestSpec = mock(org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec::class.java)
        val callSpec = mock(org.springframework.ai.chat.client.ChatClient.CallResponseSpec::class.java)

        `when`(chatClientBuilder.build()).thenReturn(chatClient)
        `when`(chatClient.prompt()).thenReturn(requestSpec)
        `when`(requestSpec.system(anyString())).thenReturn(requestSpec)
        `when`(requestSpec.user(anyString())).thenReturn(requestSpec)
        `when`(requestSpec.call()).thenReturn(callSpec)
        `when`(callSpec.content()).thenReturn(mockJson)

        val service = DocumentParserServiceImpl(
            extractors = listOf(pdfExtractor),
            chatClientBuilder = chatClientBuilder
        )

        val resumeText = """
            John Doe
            Software Engineer at TechCorp (2020-2023)
            - Built REST APIs
            - Led code reviews
            Junior Developer at StartupXYZ (2018-2020)
            - Developed frontend components
            Education: BS Computer Science, University of Technology (2014-2018)
            Skills: Kotlin, Java, TypeScript, Git, Docker, AWS
        """.trimIndent()

        val result = service.parseResumeData(resumeText)

        assertEquals(2, result.workExperiences.size)
        assertEquals("Software Engineer", result.workExperiences[0].jobTitle)
        assertEquals("TechCorp", result.workExperiences[0].company)
        assertEquals(2, result.workExperiences[0].bullets.size)
        assertEquals("Built REST APIs", result.workExperiences[0].bullets[0].content)
        assertEquals("Junior Developer", result.workExperiences[1].jobTitle)
        assertEquals(1, result.workExperiences[1].bullets.size)

        assertEquals(1, result.education.size)
        assertEquals("University of Technology", result.education[0].school)
        assertEquals(3, result.education[0].courses.size)

        assertEquals(2, result.skillGroups.size)
        assertEquals("Programming Languages", result.skillGroups[0].category)

        val userCaptor = ArgumentCaptor.forClass(String::class.java)
        verify(requestSpec).user(userCaptor.capture())
        val userPrompt = userCaptor.value
        assertTrue(userPrompt.contains("---BEGIN RESUME TEXT---"))
        assertTrue(userPrompt.contains("---END RESUME TEXT---"))
        assertTrue(userPrompt.contains(resumeText.trim()))
    }

    @Test
    fun `parseResumeData handles empty LLM response gracefully`() {
        val chatClientBuilder = mock(org.springframework.ai.chat.client.ChatClient.Builder::class.java)
        val chatClient = mock(org.springframework.ai.chat.client.ChatClient::class.java)
        val requestSpec = mock(org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec::class.java)
        val callSpec = mock(org.springframework.ai.chat.client.ChatClient.CallResponseSpec::class.java)

        `when`(chatClientBuilder.build()).thenReturn(chatClient)
        `when`(chatClient.prompt()).thenReturn(requestSpec)
        `when`(requestSpec.system(anyString())).thenReturn(requestSpec)
        `when`(requestSpec.user(anyString())).thenReturn(requestSpec)
        `when`(requestSpec.call()).thenReturn(callSpec)
        `when`(callSpec.content()).thenReturn(null)

        val service = DocumentParserServiceImpl(
            extractors = listOf(mock(ResumeTextExtractor::class.java)),
            chatClientBuilder = chatClientBuilder
        )

        val result = service.parseResumeData("some text")
        assertNotNull(result)
        assertTrue(result.workExperiences.isEmpty())
        assertTrue(result.education.isEmpty())
        assertTrue(result.skillGroups.isEmpty())
    }

    @Test
    fun `parseResumeData strips markdown code fences`() {
        val fencedJson = "```json\n{\"work_experiences\": [], \"education\": [], \"skill_groups\": []}\n```"

        val chatClientBuilder = mock(org.springframework.ai.chat.client.ChatClient.Builder::class.java)
        val chatClient = mock(org.springframework.ai.chat.client.ChatClient::class.java)
        val requestSpec = mock(org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec::class.java)
        val callSpec = mock(org.springframework.ai.chat.client.ChatClient.CallResponseSpec::class.java)

        `when`(chatClientBuilder.build()).thenReturn(chatClient)
        `when`(chatClient.prompt()).thenReturn(requestSpec)
        `when`(requestSpec.system(anyString())).thenReturn(requestSpec)
        `when`(requestSpec.user(anyString())).thenReturn(requestSpec)
        `when`(requestSpec.call()).thenReturn(callSpec)
        `when`(callSpec.content()).thenReturn(fencedJson)

        val service = DocumentParserServiceImpl(
            extractors = listOf(mock(ResumeTextExtractor::class.java)),
            chatClientBuilder = chatClientBuilder
        )

        val result = service.parseResumeData("some text")
        assertNotNull(result)
        assertTrue(result.workExperiences.isEmpty())
    }

    @Test
    fun `extractText delegates to supporting extractor`() {
        val extractor = mock(ResumeTextExtractor::class.java)
        val chatClientBuilder = mock(org.springframework.ai.chat.client.ChatClient.Builder::class.java)
        `when`(chatClientBuilder.build()).thenReturn(mock(org.springframework.ai.chat.client.ChatClient::class.java))

        val testInput = "fake content".byteInputStream()
        `when`(extractor.supports("application/pdf")).thenReturn(true)
        `when`(extractor.extractText(testInput)).thenReturn("Mocked PDF text")

        val service = DocumentParserServiceImpl(
            extractors = listOf(extractor),
            chatClientBuilder = chatClientBuilder
        )

        val result = service.extractText(testInput, "application/pdf")
        assertEquals("Mocked PDF text", result)
        verify(extractor).extractText(testInput)
    }

    @Test
    fun `extractText throws for unsupported content type`() {
        val extractor = mock(ResumeTextExtractor::class.java)
        val chatClientBuilder = mock(org.springframework.ai.chat.client.ChatClient.Builder::class.java)
        `when`(chatClientBuilder.build()).thenReturn(mock(org.springframework.ai.chat.client.ChatClient::class.java))

        `when`(extractor.supports("text/plain")).thenReturn(false)

        val service = DocumentParserServiceImpl(
            extractors = listOf(extractor),
            chatClientBuilder = chatClientBuilder
        )

        assertThrows(IllegalArgumentException::class.java) {
            service.extractText("fake".byteInputStream(), "text/plain")
        }
    }
}