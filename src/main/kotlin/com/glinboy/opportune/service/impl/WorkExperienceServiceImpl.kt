package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.WorkExperienceBulletDTO
import com.glinboy.opportune.dto.WorkExperienceDTO
import com.glinboy.opportune.entity.WorkExperience
import com.glinboy.opportune.entity.WorkExperienceBullet
import com.glinboy.opportune.mapper.WorkExperienceMapper
import com.glinboy.opportune.repository.WorkExperienceBulletRepository
import com.glinboy.opportune.repository.WorkExperienceRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.WorkExperienceService
import jakarta.persistence.EntityManager
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.*

@Service
class WorkExperienceServiceImpl(
	workExperienceRepository: WorkExperienceRepository,
	mapper: WorkExperienceMapper,
	private val bulletRepository: WorkExperienceBulletRepository,
	private val entityManager: EntityManager
) : GenericServiceImpl<UUID, WorkExperience, WorkExperienceDTO, WorkExperienceRepository,
		WorkExperienceMapper>(workExperienceRepository, mapper), WorkExperienceService {

	override fun currentUserSpecification() =
		createCurrentUserSpecification { it.get<WorkExperience>("profile").get("id") }

	override fun validateOwnership(dto: WorkExperienceDTO) {
		if (dto.profileId != null && dto.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You are not allowed to save or update Work Experience for another user"
			)
		}
	}

	@Transactional
	override fun save(d: WorkExperienceDTO): WorkExperienceDTO {
		val savedDto = super.save(d)
		if (d.bullets.isNotEmpty() && savedDto.id != null) {
			replaceBullets(savedDto.id!!, d.bullets)
			entityManager.flush()
			entityManager.clear()
			return super.findByIdForCurrentUser(savedDto.id!!).orElse(savedDto)
		}
		return savedDto
	}

	@Transactional
	override fun replaceBullets(workExperienceId: UUID, bullets: List<WorkExperienceBulletDTO>): List<WorkExperienceBulletDTO> {
		val workExperience = currentUserSpecification()
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("id"), workExperienceId)
			}
			.let { spec ->
				repository.findOne(spec)
					.orElseThrow { NoSuchElementException("WorkExperience with id $workExperienceId not found") }
			}

		bulletRepository.deleteAll(workExperience.bullets)
		bulletRepository.flush()

		val newBullets = bullets.map { bulletDto ->
			WorkExperienceBullet(
				id = null,
				content = bulletDto.content,
				displayOrder = bulletDto.displayOrder,
				createdDate = Instant.now(),
				lastModifiedDate = Instant.now(),
				workExperience = workExperience
			)
		}

		val savedBullets = bulletRepository.saveAll(newBullets)
		bulletRepository.flush()
		entityManager.clear()
		return savedBullets.map { bullet ->
			WorkExperienceBulletDTO(
				id = bullet.id,
				content = bullet.content,
				displayOrder = bullet.displayOrder,
				createdDate = bullet.createdDate,
				lastModifiedDate = bullet.lastModifiedDate
			)
		}
	}
}
