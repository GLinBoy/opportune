package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.BaseDTO
import com.glinboy.opportune.entity.BaseEntity
import com.glinboy.opportune.mapper.GenericMapper
import com.glinboy.opportune.service.GenericChildService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.transaction.annotation.Transactional
import java.util.*

abstract class GenericChildServiceImpl<ID : Any, E : BaseEntity, D : BaseDTO, R, M : GenericMapper<D, E>>(
	override val repository: R,
	override val mapper: M,
) : GenericServiceImpl<ID, E, D, R, M>(repository, mapper), GenericChildService<ID, D>
	where R : JpaRepository<E, ID>, R : JpaSpecificationExecutor<E> {

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
			Specification.allOf<E>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<ID>(this.getParentFieldName()).get<ID>(this.getIdFieldName()), parentID)
				}
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<ID>(this.getIdFieldName()), id)
				}
		)
	}

	fun getIdFieldName(): String = "id"
	abstract fun getParentFieldName(): String
}
