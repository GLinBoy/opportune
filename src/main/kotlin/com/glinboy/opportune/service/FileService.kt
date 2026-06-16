package com.glinboy.opportune.service

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface FileService {
	fun upload()
	fun download()
	fun delete()
	fun uploadResume(file: MultipartFile): String
	fun uploadAvatar(profileId: UUID, file: MultipartFile): String
	fun deleteAvatar(filePath: String)
	fun getAvatar(path: String): Resource
	fun uploadAttachment(file: MultipartFile, userId: String, noteId: String): String
	fun loadFileAsResource(path: String): Resource
	fun deleteFile(path: String)
}
