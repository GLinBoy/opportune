package com.glinboy.opportune.service.impl

import com.glinboy.opportune.service.GenericService
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

abstract class GenericServiceImpl<T: Any, E: Any, ID: Any, S: JpaRepository<E, ID>>(
	protected val repository: S,
	protected val mapper: ModelMapper,
	private val clazzT: Class<T>,
	private val clazzE: Class<E>
) : GenericService<T, ID> {

	override fun save(t: T): T {
		val entity: E = mapper.map(t, clazzE)
		val savedEntity: E = repository.save(entity)
		return mapper.map(savedEntity, clazzT)
	}

	override fun saveAll(entities: List<T>): List<T> {
		val entityList: List<E> = entities.map { mapper.map(it, clazzE) }
		val savedEntities: List<E> = repository.saveAll(entityList)
		return savedEntities.map { mapper.map(it, clazzT) }
	}

	override fun findById(id: ID): Optional<T> {
		val entity: Optional<E> = repository.findById(id)
		return entity.map { mapper.map(it, clazzT) }
	}

	override fun getById(id: ID): T {
		return this.findById(id)
			.map { mapper.map(it, clazzT) }
			.orElseThrow { NoSuchElementException("Entity with id $id not found") }
	}

	override fun findAll(pageable: Pageable): Page<T> {
		return repository.findAll(pageable)
			.map { mapper.map(it, clazzT) }
	}

	override fun update(t: T): T {
		val entity: E = mapper.map(t, clazzE)
		val updatedEntity: E = repository.save(entity)
		return mapper.map(updatedEntity, clazzT)
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
