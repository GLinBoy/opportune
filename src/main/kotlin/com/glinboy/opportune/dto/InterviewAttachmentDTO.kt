package com.glinboy.opportune.dto

import java.util.*

data class InterviewAttachmentDTO(
    override val id: UUID? = null,
    override val name: String? = null,
    override val path: String? = null,
    override val contentType: String? = null,
    override val contentLength: Long? = null,
    val interviewNoteId: UUID? = null
) : AttachmentDTO()
