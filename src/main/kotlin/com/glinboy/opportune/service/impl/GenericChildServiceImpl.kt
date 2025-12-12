package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.BaseDTO
import com.glinboy.opportune.entity.BaseEntity
import com.glinboy.opportune.mapper.GenericMapper
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.GenericChildService
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.DeleteSpecification
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

abstract class GenericChildServiceImpl<ID : Any, E : BaseEntity, D : BaseDTO, R, M : GenericMapper<D, E>>(
	override val repository: R,
	override val mapper: M,
) : GenericServiceImpl<ID, E, D, R, M>(repository, mapper), GenericChildService<ID, D>
	where R : JpaRepository<E, ID>, R : JpaSpecificationExecutor<E> {

	@PersistenceContext
	protected lateinit var entityManager: EntityManager

	fun getIdFieldName(): String = "id"

	abstract fun getParentFieldName(): String

	abstract fun getParentId(dto: D): ID?

	/**
	 * Returns the simple entity name of the parent entity (e.g., "Application", "Company").
	 * This is used for JPQL queries.
	 */
	abstract fun getParentEntityName(): String

	override fun findById(parentID: ID, id: ID): Optional<D> =
		repository.findOne(
			Specification.allOf<E>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<ID>(this.getParentFieldName()).get<ID>(this.getIdFieldName()), parentID)
				}
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<ID>(this.getIdFieldName()), id)
				})
			.map(mapper::toDto)

	override fun findAll(parentID: ID, pageable: Pageable): Page<D> =
		repository.findAll(
			Specification.allOf<E>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<ID>(this.getParentFieldName()).get<ID>(this.getIdFieldName()), parentID)
				}, pageable
		).map(mapper::toDto)

	@Transactional
	override fun delete(parentID: ID, id: ID) {
		repository.delete(
			DeleteSpecification.where<E> { root, criteriaBuilder ->
				criteriaBuilder.and(
					criteriaBuilder.equal(
						root.get<ID>(this.getParentFieldName()).get<ID>(this.getIdFieldName()),
						parentID
					),
					criteriaBuilder.equal(
						root.get<ID>(this.getIdFieldName()),
						id
					)
				)
			}
		)
	}

	override fun currentUserSpecification(): Specification<E> =
		createCurrentUserSpecification { root ->
			root.get<E>(getParentFieldName()).get<UUID>("profile").get("id")
		}

	/**
	 * Validates that the parent entity belongs to the current user.
	 * The parent ID field in the DTO should be validated as @NotNull at the DTO level.
	 * Uses EntityManager to query the parent entity directly.
	 */
	override fun validateOwnership(d: D) {
		val parentId = getParentId(d) ?: throw IllegalArgumentException("Parent ID is required")
		val currentUserId = SecurityUtils.getCurrentUserLoginID()

		// Query parent entity directly using JPQL
		val query = entityManager.createQuery(
			"""
			SELECT COUNT(p) FROM ${getParentEntityName()} p
			WHERE p.id = :parentId AND p.profile.id = :userId
			""".trimIndent(),
			Long::class.java
		)
		query.setParameter("parentId", parentId)
		query.setParameter("userId", currentUserId)

		val count = query.singleResult
		if (count == 0L) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"Parent not found or you do not have permission to access this resource"
			)
		}
	}

	override fun findByIdForCurrentUser(parentID: ID, id: ID): Optional<D> =
		repository.findOne(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<ID>(this.getParentFieldName()).get<ID>(this.getIdFieldName()), parentID)
				}
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<ID>(this.getIdFieldName()), id)
				})
			.map(mapper::toDto)

	override fun findAllForCurrentUser(parentID: ID, pageable: Pageable): Page<D> =
		repository.findAll(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<ID>(this.getParentFieldName()).get<ID>(this.getIdFieldName()), parentID)
				}, pageable
		).map(mapper::toDto)

	override fun deleteForCurrentUser(parentID: ID, id: ID) {
		repository.delete(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<ID>(this.getParentFieldName()).get<ID>(this.getIdFieldName()), parentID)
				}
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<ID>(this.getIdFieldName()), id)
				}
				.toDeleteSpecification()
		)
	}
}
