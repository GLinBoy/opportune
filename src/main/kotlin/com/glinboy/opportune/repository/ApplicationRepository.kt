package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.projection.ApplicationProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApplicationRepository : JpaRepository<Application, UUID>, JpaSpecificationExecutor<Application> {

	@Query("SELECT a.id AS id, " +
		" a.url AS url, " +
		" a.title AS title, " +
		" a.location AS location, " +
		" a.appliedAt AS appliedAt, " +
		" a.salary AS salary, " +
		" a.note AS note, " +
		" a.status AS status, " +
		" a.company.id AS companyId, " +
		" a.company.createdDate as createdDate, " +
		" a.company.lastModifiedDate as lastModifiedDate, " +
		" c.name AS companyName " +
		" FROM Application a " +
		" LEFT JOIN a.company c")
	fun findAllApplications(pageable: Pageable): Page<ApplicationProjection>

	@Query("SELECT a FROM Application a " +
		" LEFT JOIN FETCH a.company " +
		" LEFT JOIN FETCH a.timeline " +
		" LEFT JOIN FETCH a.interviewNotes " +
		" LEFT JOIN FETCH a.metadata " +
		" LEFT JOIN FETCH a.resume " +
		" LEFT JOIN FETCH a.attachments " +
		" WHERE a.id = :id")
	fun findApplicationDetailsById(id: UUID): Optional<Application>
}
