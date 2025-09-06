package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Application
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ApplicationRepository : JpaRepository<Application, UUID>

