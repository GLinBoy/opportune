package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ApplicationMetaData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApplicationMetaDataRepository : JpaRepository<ApplicationMetaData, UUID>

