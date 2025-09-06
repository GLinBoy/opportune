package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.ApplicationMetaData
import com.glinboy.opportune.entity.id.ApplicationMetaDataId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApplicationMetaDataRepository : JpaRepository<ApplicationMetaData, ApplicationMetaDataId>

