package com.glinboy.opportune.dto

abstract class MetaDataDTO : AuditableDTO() {
	open val metaName: String? = null
	open val metaValue: String? = null
}
