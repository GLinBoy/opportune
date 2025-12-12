package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.InterviewAttachmentDTO
import com.glinboy.opportune.entity.InterviewAttachment
import com.glinboy.opportune.mapper.InterviewAttachmentMapper
import com.glinboy.opportune.repository.InterviewAttachmentRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.InterviewAttachmentService
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class InterviewAttachmentServiceImpl(
	interviewAttachmentRepository: InterviewAttachmentRepository,
	mapper: InterviewAttachmentMapper,
	private val entityManager: EntityManager
) : GenericServiceImpl<UUID, InterviewAttachment, InterviewAttachmentDTO, InterviewAttachmentRepository,
	InterviewAttachmentMapper>(interviewAttachmentRepository, mapper), InterviewAttachmentService {

	override fun findByApplicationIdANdInterviewNoteIdAndId(applicationId: UUID, interviewNoteId: UUID, id: UUID):
		Optional<InterviewAttachmentDTO> = repository.findOne(
		Specification.allOf<InterviewAttachment>()
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("application").get<UUID>("id"), applicationId)
			}
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("id"), interviewNoteId)
			}
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("id"), id)
			})
		.map(mapper::toDto)

	override fun findByApplicationIdAndInterviewNoteId(applicationId: UUID, interviewNoteId: UUID, pageable: Pageable):
		Page<InterviewAttachmentDTO> = repository.findAll(
		Specification.allOf<InterviewAttachment>()
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("application").get<UUID>("id"), applicationId)
			}
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("id"), interviewNoteId)
			}, pageable
	).map(mapper::toDto)

	@Transactional
	override fun deleteByApplicationIdAndInterviewNoteIdAndId(applicationId: UUID, interviewNoteId: UUID, id: UUID) {
		repository.delete(
			Specification.allOf<InterviewAttachment>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("application").get<UUID>("id"), applicationId)
				}
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("id"), interviewNoteId)
				}
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("id"), id)
				}
				.toDeleteSpecification()
		)
	}

	override fun currentUserSpecification(): Specification<InterviewAttachment> =
		createCurrentUserSpecification { root ->
			root.get<InterviewAttachment>("interviewNote").get<UUID>("application").get<UUID>("profile").get("id")
		}

	override fun validateOwnership(interviewAttachmentDTO: InterviewAttachmentDTO) {
		val parentId = interviewAttachmentDTO.interviewNoteId ?: throw IllegalArgumentException("Parent ID is required")
		val currentUserId = SecurityUtils.getCurrentUserLoginID()

		// Query parent entity directly using JPQL
		val query = entityManager.createQuery(
			"""
			SELECT COUNT(p) FROM InterviewNote p
			WHERE p.id = :parentId AND p.interviewNote.application.profile.id = :userId
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

	override fun findByApplicationIdANdInterviewNoteIdAndIdForCurrentUser(
		applicationId: UUID,
		interviewNoteId: UUID, id: UUID
	): Optional<InterviewAttachmentDTO> = repository.findOne(
		currentUserSpecification()
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("application").get<UUID>("id"), applicationId)
			}
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("id"), interviewNoteId)
			}
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("id"), id)
			})
		.map(mapper::toDto)

	override fun findByApplicationIdAndInterviewNoteIdForCurrentUser(
		applicationId: UUID,
		interviewNoteId: UUID, pageable: Pageable
	): Page<InterviewAttachmentDTO> = repository.findAll(
		currentUserSpecification()
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("application").get<UUID>("id"), applicationId)
			}
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("id"), interviewNoteId)
			}, pageable
	).map(mapper::toDto)

	override fun deleteByApplicationIdAndInterviewNoteIdAndIdForCurrentUser(
		applicationId: UUID,
		interviewNoteId: UUID, id: UUID
	) {
		repository.delete(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("application").get<UUID>("id"), applicationId)
				}
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("interviewNote").get<UUID>("id"), interviewNoteId)
				}
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("id"), id)
				}
				.toDeleteSpecification()
		)
	}
}
