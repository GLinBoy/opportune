package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.projection.ProfileRegistrationTrendProjection
import com.glinboy.opportune.projection.ProfileStatusCountProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface ProfileRepository : JpaRepository<Profile, UUID>, JpaSpecificationExecutor<Profile> {
	fun findOneByIdAndStatus(id: UUID, status: AccountStatus): Optional<Profile>

	fun findOneByEmailIgnoreCase(email: String): Optional<Profile>

	fun findOneByEmailIgnoreCaseAndStatus(email: String, status: AccountStatus): Optional<Profile>

	@Modifying
	@Query("UPDATE Profile p SET p.emailVerification = :emailVerification WHERE p.id = :id")
	fun updateEmailVerification(id: UUID, emailVerification: Boolean)

	@Modifying
	@Query("UPDATE Profile p SET p.status = :status WHERE p.id = :id")
	fun updateStatusById(@Param("id") id: UUID, @Param("status") status: AccountStatus)

	@Modifying
	@Query("UPDATE Profile p SET p.password = :password WHERE p.id = :id")
	fun updatePassword(id: UUID, password: String)

	@Modifying
	@Query("UPDATE Profile p SET p.avatar = :avatar WHERE p.id = :id")
	fun updateAvatar(@Param("id") id: UUID, @Param("avatar") avatar: String?)

	@Query("SELECT p.status AS status, COUNT(p) AS total FROM Profile p GROUP BY p.status")
	fun countByStatusGrouped(): List<ProfileStatusCountProjection>

	@Query("""
		SELECT CAST(p.createdDate AS date) AS registrationDate, COUNT(p) AS total
		FROM Profile p
		WHERE p.createdDate >= :from
		GROUP BY CAST(p.createdDate AS date)
		ORDER BY registrationDate ASC
	""")
	fun findRegistrationTrend(@Param("from") from: Instant): List<ProfileRegistrationTrendProjection>
}

