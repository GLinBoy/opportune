package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.projection.AiQueueProjection
import com.glinboy.opportune.projection.ApplicationProjection
import com.glinboy.opportune.projection.ApplicationStatProjection
import com.glinboy.opportune.projection.ApplicationStatusCountProjection
import com.glinboy.opportune.projection.ScoreSummaryProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface ApplicationRepository : JpaRepository<Application, UUID>, JpaSpecificationExecutor<Application> {

	fun countByProfile_Id(profileId: UUID): Long

	fun countByCompany_Id(companyId: UUID): Long


	@Query("SELECT a.id AS id, " +
		" a.url AS url, " +
		" a.title AS title, " +
		" a.location AS location, " +
		" a.appliedAt AS appliedAt, " +
		" a.salary AS salary, " +
		" a.note AS note, " +
		" a.status AS status, " +
		" a.createdDate as createdDate, " +
		" a.lastModifiedDate as lastModifiedDate, " +
		" a.company.id AS companyId, " +
		" c.name AS companyName " +
		" FROM Application a " +
		" LEFT JOIN a.company c")
	fun findAllApplications(pageable: Pageable): Page<ApplicationProjection>

	@Query("SELECT a.id AS id, " +
		" a.url AS url, " +
		" a.title AS title, " +
		" a.location AS location, " +
		" a.appliedAt AS appliedAt, " +
		" a.salary AS salary, " +
		" a.note AS note, " +
		" a.status AS status, " +
		" a.createdDate as createdDate, " +
		" a.lastModifiedDate as lastModifiedDate, " +
		" a.company.id AS companyId, " +
		" c.name AS companyName " +
		" FROM Application a " +
		" LEFT JOIN a.company c " +
		" WHERE a.profile.id = :profileId ")
	fun findAllApplicationsByProfileId(profileId: UUID, pageable: Pageable): Page<ApplicationProjection>

	@Query("SELECT a FROM Application a " +
		" LEFT JOIN FETCH a.company " +
		" LEFT JOIN FETCH a.timeline " +
		" LEFT JOIN FETCH a.interviewNotes " +
		" LEFT JOIN FETCH a.metadata " +
		" LEFT JOIN FETCH a.resume " +
		" LEFT JOIN FETCH a.attachments " +
		" WHERE a.id = :id")
	fun findApplicationDetailsById(id: UUID): Optional<Application>

	@Query("SELECT a FROM Application a " +
		" LEFT JOIN FETCH a.company " +
		" LEFT JOIN FETCH a.timeline " +
		" LEFT JOIN FETCH a.interviewNotes " +
		" LEFT JOIN FETCH a.metadata " +
		" LEFT JOIN FETCH a.resume " +
		" LEFT JOIN FETCH a.attachments " +
		" WHERE a.id = :id " +
		" AND a.profile.id = :profileId ")
	fun findApplicationDetailsByProfileIdAndId(profileId: UUID, id: UUID): Optional<Application>

	@Query("""
    SELECT CAST(a.createdDate AS date) AS createdDay,
           a.status                   AS status,
           COUNT(a)                   AS total
    FROM Application a
    WHERE a.createdDate >= :from
      AND a.profile.id = :userId
    GROUP BY CAST(a.createdDate AS date), a.status
    ORDER BY createdDay ASC
""")
	fun findApplicationStatsByDateAndStatus(@Param("from") from: Instant, @Param("userId")  userId: UUID): List<ApplicationStatProjection>

	@Query("""
    SELECT AVG(a.resumeOverallScore)    AS avgResumeScore,
           AVG(a.skillMatchScore)       AS avgSkillScore,
           AVG(a.experienceMatchScore)  AS avgExperienceScore,
           AVG(a.educationMatchScore)   AS avgEducationScore,
           AVG(a.keywordMatchScore)     AS avgKeywordScore
    FROM Application a
    WHERE a.profile.id = :userId
      AND a.createdDate >= :from
""")
	fun avgScoresForProfile(@Param("from") from: Instant, @Param("userId")  userId: UUID): ScoreSummaryProjection

	@Query("SELECT a.status AS status, COUNT(a) AS total FROM Application a GROUP BY a.status")
	fun countAllByStatusGrouped(): List<ApplicationStatusCountProjection>

	@Query("""
		SELECT a.id AS id, a.title AS title, a.createdDate AS createdDate,
		       c.name AS companyName
		FROM Application a
		LEFT JOIN a.company c
		WHERE a.status = 'AI_PROCESSING'
		ORDER BY a.createdDate ASC
	""")
	fun findAiQueueItems(pageable: Pageable): List<AiQueueProjection>

	@Query(
		value = """
			SELECT a.id AS id, a.title AS title, a.createdDate AS createdDate,
			       c.name AS companyName
			FROM Application a
			LEFT JOIN a.company c
			WHERE a.status = 'AI_PROCESSING'
		""",
		countQuery = "SELECT COUNT(a) FROM Application a WHERE a.status = 'AI_PROCESSING'"
	)
	fun findAiQueueItemsPaged(pageable: Pageable): Page<AiQueueProjection>

	@Query("SELECT a.resumeOverallScore FROM Application a WHERE a.resumeOverallScore IS NOT NULL")
	fun findAllResumeScores(): List<Int>
}
