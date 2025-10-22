package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.SearchResultDTO
import com.glinboy.opportune.service.SearchService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class SearchServiceImpl(private val jdbcTemplate: JdbcTemplate) : SearchService {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	override fun search(query: String, pageable: Pageable): Page<SearchResultDTO> {
		val searchPattern = "%$query%"
		val countQuery = """
			SELECT COUNT(*) FROM (
				SELECT ID FROM COMPANY WHERE NAME iLIKE ?
				UNION ALL
				SELECT ID FROM APPLICATION WHERE TITLE iLIKE ?
			) AS combined
		""".trimIndent()
		val resultQuery = """
			SELECT * FROM (
				SELECT ID, NAME, STATUS, 'company' AS TYPE FROM COMPANY WHERE NAME iLIKE ?
				UNION ALL
				SELECT ID, TITLE AS NAME, STATUS, 'application' AS TYPE FROM APPLICATION WHERE TITLE iLIKE ?
			) AS combined
			ORDER BY LAST_MODIFIED_DATE, CREATED_DATE LIMIT ? OFFSET ?
		""".trimIndent()
		val total = jdbcTemplate.queryForObject(countQuery, Long::class.java, searchPattern, searchPattern) ?: 0L
		val results = jdbcTemplate.query(resultQuery, { rs, _ ->
			SearchResultDTO(
				id = UUID.fromString(rs.getString("ID")),
				name = rs.getString("NAME"),
				status = rs.getString("STATUS"),
				type = rs.getString("TYPE")
			)
		}, searchPattern, searchPattern, pageable.pageSize, pageable.offset)
		return PageImpl(results, PageRequest.of(pageable.pageNumber, pageable.pageSize), total)
	}

	override fun searchCompanies(query: String, pageable: Pageable): Page<SearchResultDTO> {
		val searchPattern = "%$query%"
		val countQuery = "SELECT COUNT(ID) FROM COMPANY WHERE NAME iLIKE ?"
		val resultQuery = """
				SELECT ID, NAME, STATUS, 'company' AS TYPE
				FROM COMPANY
				WHERE NAME iLIKE ?
				ORDER BY LAST_MODIFIED_DATE, CREATED_DATE LIMIT ? OFFSET ?
			""".trimIndent()
		val total = jdbcTemplate.queryForObject(countQuery, Long::class.java, searchPattern) ?: 0L
		val results = jdbcTemplate.query(resultQuery, { rs, _ ->
			SearchResultDTO(
				id = UUID.fromString(rs.getString("ID")),
				name = rs.getString("NAME"),
				status = rs.getString("STATUS"),
				type = rs.getString("TYPE")
			)
		}, searchPattern, pageable.pageSize, pageable.offset)
		return PageImpl(results, PageRequest.of(pageable.pageNumber, pageable.pageSize), total)
	}

	override fun searchApplications(query: String, pageable: Pageable): Page<SearchResultDTO> {
		val searchPattern = "%$query%"
		val countQuery = "SELECT COUNT(ID) FROM APPLICATION WHERE TITLE iLIKE ?"
		val resultQuery = """
				SELECT ID, TITLE, STATUS, 'application' AS TYPE
				FROM APPLICATION
				WHERE TITLE iLIKE ?
				ORDER BY LAST_MODIFIED_DATE, CREATED_DATE LIMIT ? OFFSET ?
			""".trimIndent()
		val total = jdbcTemplate.queryForObject(countQuery, Long::class.java, searchPattern) ?: 0L
		val results = jdbcTemplate.query(resultQuery, { rs, _ ->
			SearchResultDTO(
				id = UUID.fromString(rs.getString("ID")),
				name = rs.getString("TITLE"),
				status = rs.getString("STATUS"),
				type = rs.getString("TYPE")
			)
		}, searchPattern, pageable.pageSize, pageable.offset)
		return PageImpl(results, PageRequest.of(pageable.pageNumber, pageable.pageSize), total)
	}

}
