package com.glinboy.opportune.service.impl

import com.glinboy.opportune.config.ApplicationProperties
import com.glinboy.opportune.dto.AiAnalysisResultDTO
import com.glinboy.opportune.dto.ApplicationMetaDataDTO
import com.glinboy.opportune.enums.ApplicationStatus
import com.glinboy.opportune.event.ApplicationSubmittedEvent
import com.glinboy.opportune.service.*
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import tools.jackson.databind.ObjectMapper
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class AiServiceImpl(
	private val profileService: ProfileService,
	private val applicationService: ApplicationService,
	private val resumeService: ProfileResumeService,
	private val companyService: CompanyService,
	private val applicationMetaDataService: ApplicationMetaDataService,
	private val fileService: FileService,
	private val resumeTextFormatterService: ResumeTextFormatterService,
	private val properties: ApplicationProperties,
	private val objectMapper: ObjectMapper,
	chatClientBuilder: ChatClient.Builder,
): AiService {

	private val log = LoggerFactory.getLogger(this::class.java)

	private val chatClient: ChatClient = chatClientBuilder.build()

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun onApplicationSubmitted(event: ApplicationSubmittedEvent) {
		log.info("Received ApplicationSubmittedEvent for application {}, starting AI analysis...", event.applicationId)
		analysisApplication(event.applicationId)
	}

	@Async
	override fun retryAnalysis(applicationId: UUID) {
		log.info("Admin-triggered retry for application {}", applicationId)
		analysisApplication(applicationId)
	}

	override fun analysisApplication(applicationId: UUID) {
		val applicationDTO = applicationService.getById(applicationId)
		val profileDTO = profileService.getById(applicationDTO.profileId!!)
		val resumeDTO = resumeService.findByProfileId(applicationDTO.profileId!!).orElseThrow()

		log.info("Loaded Application: ${applicationDTO.id}, Profile: ${profileDTO.id}, Resume: ${resumeDTO.id}")

		// Pre-process: strip HTML from raw job description
		val cleanJobDescription = if (!applicationDTO.rawContent.isNullOrBlank()) {
			Jsoup.parse(applicationDTO.rawContent).text()
		} else {
			applicationDTO.rawContent ?: ""
		}

		// Pre-process: try structured resume text first, fall back to PDFBox
		val extractedResumeText: String = resumeTextFormatterService.formatResumeAsText(applicationDTO.profileId!!)
			.ifEmpty {
				if (resumeDTO.path != null) {
					try {
						val resource = fileService.loadFileAsResource(resumeDTO.path)
						val bytes = resource.inputStream.use { it.readBytes() }
						Loader.loadPDF(bytes).use { doc -> PDFTextStripper().getText(doc) }
					} catch (e: Exception) {
						log.warn("Could not extract text from resume PDF at ${resumeDTO.path}: ${e.message}")
						""
					}
				} else ""
			}

		// Load prompt templates
		val currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

		// Read system prompt as plain text to avoid StringTemplate parsing errors
		// caused by curly braces in the embedded JSON schema
		val systemPromptText = ClassPathResource("prompts/ai-analysis-system.st")
			.inputStream.bufferedReader().readText()
			.replace("{currentDate}", currentDate)

		val userPromptTemplate = PromptTemplate(ClassPathResource("prompts/ai-analysis-user.st"))
		val userMessage = userPromptTemplate.createMessage(
			mapOf(
				"cleanJobDescription" to cleanJobDescription,
				"extractedResumeText" to extractedResumeText
			)
		)

		log.info("Sending AI analysis request for application ${applicationDTO.id}...")

		// Call AI model
		val jsonResponse: String? = chatClient.prompt()
			.system(systemPromptText)
			.user(userMessage.text.orEmpty())
			.call()
			.content()

		log.info("AI analysis response for application ${applicationDTO.id}:\n$jsonResponse")

		// Write response to file for analysis
		val outputDir = Path.of(properties.files.basePath, "ai-analysis")
		Files.createDirectories(outputDir)
		val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
		val outputFile = outputDir.resolve("${applicationDTO.id}_$timestamp.json")
		Files.writeString(outputFile, jsonResponse ?: "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
		log.info("AI analysis response written to: $outputFile")

		val cleanedJson = jsonResponse
			?.trim()
			?.removePrefix("```json")
			?.removePrefix("```")
			?.removeSuffix("```")
			?.trim()
			?: "{}"

		val analysisResultDTO = objectMapper.readValue(cleanedJson, AiAnalysisResultDTO::class.java)
		val company = companyService.findByNameInternal(analysisResultDTO.companyName!!)

		val updatedApplicationDTO = applicationDTO.copy(
			title = analysisResultDTO.title ?: applicationDTO.title,
			location = analysisResultDTO.location ?: applicationDTO.location,
			description = analysisResultDTO.shortDescription ?: applicationDTO.description,
			coverLetter = analysisResultDTO.coverLetter ?: applicationDTO.coverLetter,
			resumeInsights = analysisResultDTO.resumeInsights ?: applicationDTO.resumeInsights,
			interviewIntroduction = analysisResultDTO.interviewIntroduction ?: applicationDTO.interviewIntroduction,
			resumeOverallScore = analysisResultDTO.resumeMatch.overallScore ?: applicationDTO.resumeOverallScore,
			skillMatchScore = analysisResultDTO.resumeMatch.skillsMatch.score ?: applicationDTO.skillMatchScore,
			skillMatchRationale = analysisResultDTO.resumeMatch.skillsMatch.rationale ?: applicationDTO.skillMatchRationale,
			experienceMatchScore = analysisResultDTO.resumeMatch.experienceMatch.score ?: applicationDTO.experienceMatchScore,
			experienceMatchRationale = analysisResultDTO.resumeMatch.experienceMatch.rationale ?: applicationDTO.experienceMatchRationale,
			educationMatchScore = analysisResultDTO.resumeMatch.educationMatch.score ?: applicationDTO.educationMatchScore,
			educationMatchRationale = analysisResultDTO.resumeMatch.educationMatch.rationale ?: applicationDTO.educationMatchRationale,
			keywordMatchScore = analysisResultDTO.resumeMatch.keywordsMatch.score ?: applicationDTO.keywordMatchScore,
			keywordMatchRationale = analysisResultDTO.resumeMatch.keywordsMatch.rationale ?: applicationDTO.keywordMatchRationale,
			status = ApplicationStatus.READY_TO_APPLY,
			companyId = company.map { it.id }.orElse(applicationDTO.companyId)
		)

		log.info("Company name: {}, Company ID: {}", analysisResultDTO.companyName, company.map { it.id }.orElse(null))

		applicationService.update(updatedApplicationDTO)

		applicationMetaDataService.saveAllInternal(analysisResultDTO.metadata.map { meta ->
			ApplicationMetaDataDTO(
				id = null,
				applicationId = applicationDTO.id!!,
				metaName = meta.key,
				metaValue = meta.value
			)
		})
		log.info("Application ${applicationDTO.id} updated with AI analysis results.")
	}
}
