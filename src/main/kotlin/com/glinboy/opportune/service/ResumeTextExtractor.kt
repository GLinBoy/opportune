package com.glinboy.opportune.service

import java.io.InputStream

interface ResumeTextExtractor {
    fun supports(contentType: String): Boolean
    fun extractText(inputStream: InputStream): String
}
