package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProfileRepository : JpaRepository<Profile, UUID>, JpaSpecificationExecutor<Profile> {
	fun findOneByEmailIgnoreCase(email: String): Optional<Profile>

	@Modifying
	@Query("UPDATE Profile p SET p.emailVerification = :emailVerification WHERE p.id = :id")
	fun updateEmailVerification(id: UUID, emailVerification: Boolean)

	@Modifying
	@Query("UPDATE Profile p SET p.password = :password WHERE p.id = :id")
	fun updatePassword(id: UUID, password: String)
}

