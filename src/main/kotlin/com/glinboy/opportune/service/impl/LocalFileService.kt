package com.glinboy.opportune.service.impl

import com.glinboy.opportune.config.ApplicationProperties
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.FileService
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class LocalFileService(private val properties: ApplicationProperties) : FileService {

	override fun upload() {
		TODO("Not yet implemented")
	}

	override fun download() {
		TODO("Not yet implemented")
	}

	override fun delete() {
		TODO("Not yet implemented")
	}

	override fun uploadResume(file: MultipartFile): String {
		val userId = SecurityUtils.getCurrentUserLoginID()
		val resumePath = Paths.get(properties.files.basePath, userId.toString(), "resumes")

		if (!Files.exists(resumePath)) {
			Files.createDirectories(resumePath)
		}

		val originalFilename = file.originalFilename ?: "resume"
		val extension = originalFilename.substringAfterLast('.', "")
		val newFilename = "${UUID.randomUUID()}${if (extension.isNotEmpty()) ".$extension" else ""}"
		val targetPath: Path = resumePath.resolve(newFilename)

		Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)

		return targetPath.toString()
	}

	override fun loadFileAsResource(path: String): Resource {
		val filePath = Paths.get(path)
		val resource = UrlResource(filePath.toUri())
		if (resource.exists() && resource.isReadable) {
			return resource
		} else {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: $path")
		}
	}

	override fun deleteFile(path: String) {
		val filePath = Paths.get(path)
		if (Files.exists(filePath)) {
			Files.delete(filePath)
		}
	}
}
