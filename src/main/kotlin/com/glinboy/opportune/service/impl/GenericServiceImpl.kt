package com.glinboy.opportune.service.impl

import com.glinboy.opportune.mapper.GenericMapper
import com.glinboy.opportune.service.GenericService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

abstract class GenericServiceImpl<D : Any, E : Any, ID : Any,
	S : JpaRepository<E, ID>, M : GenericMapper<D, E>>(
	protected val repository: S,
	protected val mapper: M,
) : GenericService<D, ID> {

	override fun save(t: D): D {
		val entity: E = mapper.toEntity(t)
		val savedEntity: E = repository.save(entity)
		return mapper.toDto(savedEntity)
	}

	override fun saveAll(entities: List<D>): List<D> {
		val entityList: List<E> = entities.map { mapper.toEntity(it) }
		val savedEntities: List<E> = repository.saveAll(entityList)
		return savedEntities.map { mapper.toDto(it) }
	}

	override fun findById(id: ID): Optional<D> {
		val entity: Optional<E> = repository.findById(id)
		return entity.map { mapper.toDto(it) }
	}

	override fun getById(id: ID): D {
		return this.findById(id)
			.orElseThrow { NoSuchElementException("Entity with id $id not found") }
	}

	override fun findAll(pageable: Pageable): Page<D> {
		return repository.findAll(pageable)
			.map { mapper.toDto(it) }
	}

	override fun update(t: D): D {
		val entity: E = mapper.toEntity(t)
		val updatedEntity: E = repository.save(entity)
		return mapper.toDto(updatedEntity)
	}

	override fun delete(id: ID) {
		repository.deleteById(id)
	}

	override fun deleteAllByIds(ids: List<ID>) {
		repository.deleteAllById(ids)
	}

	override fun existsById(id: ID): Boolean {
		return repository.existsById(id)
	}

	override fun count(): Long {
		return repository.count()
	}
}
