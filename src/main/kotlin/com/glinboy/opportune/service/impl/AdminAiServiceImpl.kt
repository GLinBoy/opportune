package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.AdminScoreDistributionDTO
import com.glinboy.opportune.dto.AiQueueItemDTO
import com.glinboy.opportune.dto.ScoreBucketDTO
import com.glinboy.opportune.enums.ApplicationStatus
import com.glinboy.opportune.repository.ApplicationRepository
import com.glinboy.opportune.service.AdminAiService
import com.glinboy.opportune.service.AiService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@Transactional(readOnly = true)
class AdminAiServiceImpl(
    private val applicationRepository: ApplicationRepository,
    private val aiService: AiService,
) : AdminAiService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getQueue(pageable: Pageable): Page<AiQueueItemDTO> =
        applicationRepository.findAiQueueItemsPaged(pageable).map { item ->
            val waitMinutes = ChronoUnit.MINUTES.between(item.getCreatedDate(), Instant.now())
            AiQueueItemDTO(
                id = item.getId(),
                title = item.getTitle(),
                companyName = item.getCompanyName(),
                createdDate = item.getCreatedDate(),
                waitMinutes = waitMinutes
            )
        }

    @Transactional
    override fun retryJob(applicationId: UUID) {
        val application = applicationRepository.findById(applicationId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found")
        }
        if (application.status != ApplicationStatus.AI_PROCESSING) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Application is not in AI_PROCESSING state (current: ${application.status})"
            )
        }
        log.info("Admin requested AI retry for application {}", applicationId)
        aiService.retryAnalysis(applicationId)
    }

    override fun getScoreDistribution(): AdminScoreDistributionDTO {
        val scores = applicationRepository.findAllResumeScores()
        val buckets = (0..9).map { bucket ->
            val low = bucket * 10
            val high = if (bucket == 9) 100 else low + 9
            val label = "$low–$high"
            val count = scores.count { it in low..high }
            ScoreBucketDTO(label, count)
        }
        return AdminScoreDistributionDTO(buckets)
    }
}
