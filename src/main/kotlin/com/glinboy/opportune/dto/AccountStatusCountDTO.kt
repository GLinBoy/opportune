package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.AccountStatus

data class AccountStatusCountDTO(
    val status: AccountStatus,
    val count: Long
)
