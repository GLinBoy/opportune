package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.ApplicationStatus

data class ApplicationStatusCountDTO(
    val status: ApplicationStatus,
    val count: Long
)
