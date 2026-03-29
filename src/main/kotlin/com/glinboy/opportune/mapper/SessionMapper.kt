package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.SessionDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.Session
import org.springframework.stereotype.Component

@Component
class SessionMapper : GenericMapper<SessionDTO, Session> {

	override fun createEntity(dto: SessionDTO): Session {
		return Session(
			refreshTokenId = dto.refreshTokenId,
			accessTokenId = dto.accessTokenId,
			accessTokenExpiration = dto.accessTokenExpiration,
			refreshTokenExpiration = dto.refreshTokenExpiration,
			status = dto.status ?: com.glinboy.opportune.enums.SessionStatus.ACTIVE,
			lastActiveAt = dto.lastActiveAt ?: java.time.Instant.now(),
			clientAgent = dto.clientAgent,
			clientIp = dto.clientIp,
			clientGeo = dto.clientGeo,
			deviceId = dto.deviceId,
			deviceType = dto.deviceType,
			os = dto.os,
			browser = dto.browser,
			isMobile = dto.isMobile,
			loginAt = dto.loginAt ?: java.time.Instant.now(),
			revokedAt = dto.revokedAt,
			revocationReason = dto.revocationReason,
			lastRefreshedAt = dto.lastRefreshedAt,
			profile = dto.profileId?.let { Profile(id = it) },
		)
	}

	override fun updateEntity(dto: SessionDTO, entity: Session): Session {
		return entity.copy(
			accessTokenId = dto.accessTokenId,
			accessTokenExpiration = dto.accessTokenExpiration,
			refreshTokenExpiration = dto.refreshTokenExpiration,
			status = dto.status ?: entity.status,
			lastActiveAt = dto.lastActiveAt ?: entity.lastActiveAt,
			clientAgent = dto.clientAgent,
			clientIp = dto.clientIp,
			clientGeo = dto.clientGeo,
			deviceId = dto.deviceId,
			deviceType = dto.deviceType,
			os = dto.os,
			browser = dto.browser,
			isMobile = dto.isMobile,
			revokedAt = dto.revokedAt,
			revocationReason = dto.revocationReason,
			lastRefreshedAt = dto.lastRefreshedAt,
			profile = entity.profile,
		)
	}

	override fun toDto(entity: Session): SessionDTO {
		return SessionDTO(
			refreshTokenId = entity.refreshTokenId,
			accessTokenId = entity.accessTokenId,
			accessTokenExpiration = entity.accessTokenExpiration,
			refreshTokenExpiration = entity.refreshTokenExpiration,
			status = entity.status,
			lastActiveAt = entity.lastActiveAt,
			clientAgent = entity.clientAgent,
			clientIp = entity.clientIp,
			clientGeo = entity.clientGeo,
			deviceId = entity.deviceId,
			deviceType = entity.deviceType,
			os = entity.os,
			browser = entity.browser,
			isMobile = entity.isMobile,
			loginAt = entity.loginAt,
			revokedAt = entity.revokedAt,
			revocationReason = entity.revocationReason,
			lastRefreshedAt = entity.lastRefreshedAt,
			profileId = entity.profile?.id,
		)
	}
}

