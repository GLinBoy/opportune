package com.glinboy.opportune.web.config

import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig {

	@Bean
	fun modelMapper(): ModelMapper {
		val mapper: ModelMapper = ModelMapper()
		mapper.configuration.isAmbiguityIgnored = true
		return mapper
	}
}
