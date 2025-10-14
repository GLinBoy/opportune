package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.BaseDTO
import com.glinboy.opportune.entity.BaseEntity
import com.glinboy.opportune.mapper.GenericMapper
import com.glinboy.opportune.service.GenericService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

abstract class GenericServiceImpl<ID : Any, E : BaseEntity, D : BaseDTO,
	S, M : GenericMapper<D, E>>(
	protected val repository: S,
	protected val mapper: M,
) : GenericService<ID, D>
	where S : JpaRepository<E, ID>, S : JpaSpecificationExecutor<E> {

	protected val log: Logger = LoggerFactory.getLogger(this::class.java)

	override fun save(t: D): D {
		val entity: E = mapper.createEntity(t)
		val savedEntity: E = repository.save(entity)
		return mapper.toDto(savedEntity)
	}

	override fun saveAll(entities: List<D>): List<D> {
		val entityList: List<E> = entities.map { mapper.createEntity(it) }
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

	override fun findAll(specification: Specification<Any>, pageable: Pageable): Page<D> {
		return repository.findAll(specification as Specification<E>, pageable)
			.map { mapper.toDto(it) }
	}

	override fun update(t: D): D {
		return this.repository.findById(t.id as ID)
			.map { mapper.updateEntity(t, it) }
			.map { repository.save(it) }
			.map { mapper.toDto(it) }
			.orElseThrow { NoSuchElementException("Entity with id ${t.id} not found") }
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
