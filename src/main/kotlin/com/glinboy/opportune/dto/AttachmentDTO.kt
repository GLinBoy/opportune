package com.glinboy.opportune.dto

abstract class AttachmentDTO : BaseDTO() {
	open val name: String? = null
	open val path: String? = null
	open val contentType: String? = null
	open val contentLength: Long? = null
}
