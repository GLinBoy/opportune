package com.glinboy.opportune.dto

import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.enums.AccountStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserSecurityDTO(private val profile: Profile) : UserDetails {
	override fun getAuthorities() = this.profile.roles
		.map { SimpleGrantedAuthority(it.name) }

	override fun getPassword(): String = profile.password!!

	override fun getUsername(): String = profile.email!!

	override fun isAccountNonExpired(): Boolean = true

	override fun isAccountNonLocked(): Boolean = AccountStatus.SUSPENDED == profile.status

	override fun isCredentialsNonExpired(): Boolean = true

	override fun isEnabled(): Boolean = AccountStatus.ACTIVE == profile.status
}
