package com.glinboy.opportune.mapper

interface GenericMapper<D, E> {
	fun toEntity(dto: D): E
	fun toDto(entity: E): D
}
