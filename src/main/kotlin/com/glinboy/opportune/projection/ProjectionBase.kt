package com.glinboy.opportune.projection

import com.fasterxml.jackson.annotation.JsonIgnore

interface ProjectionBase {
	// This prevents Jackson from serializing (and invoking) it
	@JsonIgnore
	fun getDecoratedClass(): Class<*>?
}
