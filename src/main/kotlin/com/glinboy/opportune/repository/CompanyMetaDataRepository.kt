package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.CompanyMetaData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CompanyMetaDataRepository : JpaRepository<CompanyMetaData, UUID>

