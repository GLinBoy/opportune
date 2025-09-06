package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.CompanyMetaData
import com.glinboy.opportune.entity.id.CompanyMetaDataId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyMetaDataRepository : JpaRepository<CompanyMetaData, CompanyMetaDataId>

