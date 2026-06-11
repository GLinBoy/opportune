package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ResumeExtractionResultDTO
import java.io.InputStream

interface DocumentParserService {
    fun extractText(inputStream: InputStream, contentType: String): String
    fun parseResumeData(resumeText: String): ResumeExtractionResultDTO
}
