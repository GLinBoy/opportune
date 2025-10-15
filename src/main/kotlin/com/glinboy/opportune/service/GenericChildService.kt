package com.glinboy.opportune.service

import com.glinboy.opportune.dto.BaseDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface GenericChildService<ID, D : BaseDTO> : GenericService<ID, D> {
	fun findById(parentID: ID, id: ID): Optional<D>
	fun findAll(parentID: ID, pageable: Pageable): Page<D>
	fun delete(parentID: ID, id: ID): Unit
}
