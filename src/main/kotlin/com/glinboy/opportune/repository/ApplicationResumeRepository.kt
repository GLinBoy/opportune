package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ApplicationResume
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ApplicationResumeRepository : JpaRepository<ApplicationResume, UUID>

