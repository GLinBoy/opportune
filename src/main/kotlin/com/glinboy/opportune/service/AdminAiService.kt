package com.glinboy.opportune.service

import com.glinboy.opportune.dto.AdminScoreDistributionDTO
import com.glinboy.opportune.dto.AiQueueItemDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface AdminAiService {
    fun getQueue(pageable: Pageable): Page<AiQueueItemDTO>
    fun retryJob(applicationId: UUID)
    fun getScoreDistribution(): AdminScoreDistributionDTO
}
