package com.glinboy.opportune.service

import com.glinboy.opportune.dto.AdminSessionListItemDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface AdminSessionService {
	fun listSessions(filter: String?, pageable: Pageable): Page<AdminSessionListItemDTO>
	fun revokeSession(id: UUID)
	fun bulkRevokeByUser(profileId: UUID)
	fun bulkRevokeByIp(ip: String)
}
