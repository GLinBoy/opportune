package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.InterviewAttachmentDTO
import com.glinboy.opportune.entity.InterviewAttachment
import com.glinboy.opportune.mapper.InterviewAttachmentMapper
import com.glinboy.opportune.repository.InterviewAttachmentRepository
import com.glinboy.opportune.service.InterviewAttachmentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class InterviewAttachmentServiceImpl(
	interviewAttachmentRepository: InterviewAttachmentRepository,
	mapper: InterviewAttachmentMapper
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
		)
	}

	override fun currentUserSpecification(): Specification<InterviewAttachment> =
		createCurrentUserSpecification { root ->
			root.get<InterviewAttachment>("interviewNote").get<UUID>("application").get<UUID>("profile").get("id")
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
		)
	}
}
