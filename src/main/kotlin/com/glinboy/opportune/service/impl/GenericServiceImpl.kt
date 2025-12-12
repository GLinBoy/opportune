package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.BaseDTO
import com.glinboy.opportune.entity.BaseEntity
import com.glinboy.opportune.mapper.GenericMapper
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.GenericService
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Root
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.DeleteSpecification
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
abstract class GenericServiceImpl<ID : Any, E : BaseEntity, D : BaseDTO,
	R, M : GenericMapper<D, E>>(
	protected open val repository: R,
	protected open val mapper: M,
) : GenericService<ID, D>
	where R : JpaRepository<E, ID>, R : JpaSpecificationExecutor<E> {

	protected val log: Logger = LoggerFactory.getLogger(this::class.java)

	/**
	 * Helper method to create a specification that filters entities by the current user's profile ID.
	 *
	 * @param profileIdPath A function that extracts the profile ID path from the entity root.
	 * @return A Specification that filters by the current user's profile ID.
	 */
	protected fun createCurrentUserSpecification(profileIdPath: (Root<E>) -> Path<UUID>): Specification<E> =
		Specification<E> { root, _, criteriaBuilder ->
			criteriaBuilder.equal(
				profileIdPath(root),
				SecurityUtils.getCurrentUserLoginID()
			)
		}

	/**
	 * Converts a Specification to a DeleteSpecification.
	 * This allows reusing existing Specification logic for delete operations.
	 */
	@Suppress("UNCHECKED_CAST")
	protected fun Specification<E>.toDeleteSpecification(): DeleteSpecification<E> =
		DeleteSpecification.where<E> { root, criteriaBuilder ->
			this.toPredicate(root as Root<E>, criteriaBuilder.createQuery(Any::class.java), criteriaBuilder)
		}

	abstract fun currentUserSpecification(): Specification<E>
	abstract fun validateOwnership(d: D)

	@Transactional
	override fun save(d: D): D {
		validateOwnership(d)
		val entity: E = mapper.createEntity(d)
		val savedEntity: E = repository.save(entity)
		return mapper.toDto(savedEntity)
	}

	@Transactional
	override fun saveAll(entities: List<D>): List<D> {
		val entityList: List<E> = entities
			.onEach { validateOwnership(it) }
			.map { mapper.createEntity(it) }
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

	@Transactional
	override fun update(d: D): D {
		return this.repository.findById(d.id as ID)
			.map { mapper.updateEntity(d, it) }
			.map { repository.save(it) }
			.map { mapper.toDto(it) }
			.orElseThrow { NoSuchElementException("Entity with id ${d.id} not found") }
	}

	@Transactional
	override fun delete(id: ID) {
		repository.deleteById(id)
	}

	@Transactional
	override fun deleteAllByIds(ids: List<ID>) {
		repository.deleteAllById(ids)
	}

	override fun existsById(id: ID): Boolean {
		return repository.existsById(id)
	}

	override fun getByIdForCurrentUser(id: ID): D {
		currentUserSpecification()
			.and(Specification<E> { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<ID>("id"), id)
			}
			).let { specification ->
				return repository.findOne(specification)
					.map { mapper.toDto(it) }
					.orElseThrow { NoSuchElementException("Entity with id $id not found") }
			}
	}

	override fun findByIdForCurrentUser(id: ID): Optional<D> {
		currentUserSpecification()
			.and(Specification<E> { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<ID>("id"), id)
			}
			).let { specification ->
				return repository.findOne(specification)
					.map { mapper.toDto(it) }
			}
	}

	override fun findAllForCurrentUser(pageable: Pageable): Page<D> =
		repository.findAll(currentUserSpecification(), pageable)
			.map { mapper.toDto(it) }

	override fun findAllForCurrentUser(specification: Specification<Any>, pageable: Pageable): Page<D> {
		val combinedSpecification = currentUserSpecification()
			.and(specification as Specification<E>)
		return repository.findAll(combinedSpecification, pageable)
			.map { mapper.toDto(it) }
	}

	@Transactional
	override fun updateForCurrentUser(d: D): D {
		currentUserSpecification()
			.and(Specification<E> { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<ID>("id"), d.id)
			}
			).let { specification ->
				return repository.findOne(specification)
					.map { mapper.updateEntity(d, it) }
					.map { repository.save(it) }
					.map { mapper.toDto(it) }
					.orElseThrow { NoSuchElementException("Entity with id ${d.id} not found") }
			}
	}

	@Transactional
	override fun deleteForCurrentUser(id: ID) {
		currentUserSpecification()
			.and(Specification<E> { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<ID>("id"), id)
			})
			.toDeleteSpecification()
			.let { specification -> repository.delete(specification) }
	}

	@Transactional
	override fun deleteAllByIdsForCurrentUser(ids: List<ID>) {
		currentUserSpecification()
			.and(Specification<E> { root, _, criteriaBuilder ->
				root.get<ID>("id").`in`(ids)
			})
			.toDeleteSpecification()
			.let { specification -> repository.delete(specification) }
	}

	override fun existsByIdForCurrentUser(id: ID): Boolean =
		currentUserSpecification()
			.and(Specification<E> { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<ID>("id"), id)
			}
			).let { specification ->
				return repository.exists(specification)
			}
}
