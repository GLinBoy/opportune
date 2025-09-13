package com.glinboy.opportune.service

import com.glinboy.opportune.dto.BaseDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface GenericService<ID, D : BaseDTO> {
	fun save(t: D): D

	fun saveAll(entities: List<D>): List<D>

	fun findById(id: ID): Optional<D>

	fun getById(id: ID): D

	fun findAll(pageable: Pageable): Page<D>

	fun update(t: D): D

	fun delete(id: ID): Unit

	fun deleteAllByIds(ids: List<ID>): Unit

	fun existsById(id: ID): Boolean

	fun count(): Long
}
