package com.glinboy.opportune.service

import com.glinboy.opportune.dto.AdminUserDetailDTO
import com.glinboy.opportune.dto.AdminUserListItemDTO
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface AdminUserService {
	fun listUsers(filter: String?, pageable: Pageable): Page<AdminUserListItemDTO>
	fun getUser(id: UUID): AdminUserDetailDTO
	fun updateStatus(id: UUID, status: AccountStatus): ProfileDTO
	fun updateRole(id: UUID, role: Role): ProfileDTO
	fun revokeSessions(id: UUID)
	fun deleteUser(id: UUID)
}
