package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Session
import com.glinboy.opportune.enums.RevocationReason
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SessionRepository : JpaRepository<Session, UUID>, JpaSpecificationExecutor<Session> {
	@Modifying
	@Query("UPDATE Session s " +
		" SET s.status = 'REVOKED', s.revocationReason = :reason, s.revokedAt = instant " +
		" WHERE s.profile.id = :profileId AND s.status = 'ACTIVE' ")
	fun revokeAllSessions(@Param("profileId") profileId: UUID, @Param("reason") reason: RevocationReason)

	@Modifying
	@Query("UPDATE Session s " +
		" SET s.status = 'REVOKED', s.revocationReason = :reason, s.revokedAt = instant " +
		" WHERE s.profile.id = :profileId AND s.status = 'ACTIVE' AND s.refreshTokenId <> :sessionId")
	fun revokeAllSessionsExcept(@Param("profileId") profileId: UUID, @Param("reason") reason: RevocationReason, @Param("sessionId") sessionId: UUID)
}

