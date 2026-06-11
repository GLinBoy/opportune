package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ResumeExtractionResultDTO
import com.glinboy.opportune.service.DocumentParserService
import com.glinboy.opportune.service.ResumeTextExtractor
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import java.io.InputStream

@Service
class DocumentParserServiceImpl(
    private val extractors: List<ResumeTextExtractor>,
    chatClientBuilder: ChatClient.Builder
) : DocumentParserService {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val chatClient: ChatClient = chatClientBuilder.build()

    private val llmObjectMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build()

    override fun extractText(inputStream: InputStream, contentType: String): String {
        val extractor = extractors.firstOrNull { it.supports(contentType) }
            ?: throw IllegalArgumentException("Unsupported content type: $contentType")
        return extractor.extractText(inputStream)
    }

    override fun parseResumeData(resumeText: String): ResumeExtractionResultDTO {
        val systemPromptText = ClassPathResource("prompts/document-parser-system.st")
            .inputStream.bufferedReader().readText()

        val userPromptText = ClassPathResource("prompts/document-parser-user.st")
            .inputStream.bufferedReader().readText()
            .replace("{resumeText}", resumeText)

        log.info("Sending resume text to LLM for structured extraction ({} chars)...", resumeText.length)

        val jsonResponse: String? = chatClient.prompt()
            .system(systemPromptText)
            .user(userPromptText)
            .call()
            .content()

        log.info("LLM extraction response received ({} chars)", jsonResponse?.length ?: 0)

        val cleanedJson = jsonResponse
            ?.trim()
            ?.removePrefix("```json")
            ?.removePrefix("```")
            ?.removeSuffix("```")
            ?.trim()
            ?: "{}"

        return llmObjectMapper.readValue(cleanedJson, ResumeExtractionResultDTO::class.java)
    }
}