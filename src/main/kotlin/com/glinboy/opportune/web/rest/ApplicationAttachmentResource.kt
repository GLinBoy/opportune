package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationAttachmentDTO
import com.glinboy.opportune.service.ApplicationAttachmentService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/application-attachments")
class ApplicationAttachmentResource(applicationAttachmentService: ApplicationAttachmentService):
    GenericResource<UUID, ApplicationAttachmentDTO, ApplicationAttachmentService>(applicationAttachmentService) {
}

