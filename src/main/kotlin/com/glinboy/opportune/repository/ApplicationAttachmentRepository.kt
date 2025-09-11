package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ApplicationAttachment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApplicationAttachmentRepository : JpaRepository<ApplicationAttachment, UUID>

