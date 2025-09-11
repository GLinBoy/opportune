package com.glinboy.opportune.dto

abstract class MetaDataDTO : BaseDTO() {
	open val metaName: String? = null
	open val metaValue: String? = null
}
