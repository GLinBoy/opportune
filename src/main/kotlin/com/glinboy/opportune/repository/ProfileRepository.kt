package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProfileRepository : JpaRepository<Profile, UUID>, JpaSpecificationExecutor<Profile>

