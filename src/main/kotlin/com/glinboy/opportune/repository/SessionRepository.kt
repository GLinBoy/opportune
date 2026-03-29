package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Session
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SessionRepository : JpaRepository<Session, UUID>, JpaSpecificationExecutor<Session>

