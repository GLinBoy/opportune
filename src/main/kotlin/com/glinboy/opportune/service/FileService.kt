package com.glinboy.opportune.service

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface FileService {
	fun upload()
	fun download()
	fun delete()
	fun uploadResume(file: MultipartFile): String
	fun uploadAttachment(file: MultipartFile, userId: String, noteId: String): String
	fun loadFileAsResource(path: String): Resource
	fun deleteFile(path: String)
}
