package com.glinboy.opportune.service

import com.glinboy.opportune.dto.SearchResultDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SearchService {
	fun search(query: String, pageable: Pageable): Page<SearchResultDTO>
	fun searchCompanies(query: String, pageable: Pageable): Page<SearchResultDTO>
	fun searchApplications(query: String, pageable: Pageable): Page<SearchResultDTO>
}
