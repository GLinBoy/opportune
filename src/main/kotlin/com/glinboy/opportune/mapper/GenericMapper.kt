package com.glinboy.opportune.mapper

interface GenericMapper<D, E> {
	fun createEntity(dto: D): E
	fun updateEntity(dto: D, entity: E): E
	fun toDto(entity: E): D
}
