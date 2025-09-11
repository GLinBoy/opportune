package com.glinboy.opportune.mapper

import com.glinboy.opportune.dto.ApplicationResumeDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.entity.ApplicationResume
import org.springframework.stereotype.Component
import java.util.*

@Component
class ApplicationResumeMapper : GenericMapper<ApplicationResumeDTO, ApplicationResume> {
    override fun createEntity(dto: ApplicationResumeDTO): ApplicationResume {
        return ApplicationResume(
            application = dto.applicationId?.let { Application(id = it) },
            id = UUID.randomUUID(),
            name = dto.name,
            path = dto.path,
            contentType = dto.contentType,
            contentLength = dto.contentLength
        )
    }

    override fun updateEntity(dto: ApplicationResumeDTO, entity: ApplicationResume): ApplicationResume {
        return ApplicationResume(
            application = entity.application ?: dto.applicationId?.let { Application(id = it) },
            id = dto.id ?: entity.id ?: UUID.randomUUID(),
            name = dto.name,
            path = dto.path,
            contentType = dto.contentType,
            contentLength = dto.contentLength
        )
    }

    override fun toDto(entity: ApplicationResume): ApplicationResumeDTO {
        return ApplicationResumeDTO(
            id = entity.id,
            name = entity.name,
            path = entity.path,
            contentType = entity.contentType,
            contentLength = entity.contentLength,
            applicationId = entity.application?.id
        )
    }
}

