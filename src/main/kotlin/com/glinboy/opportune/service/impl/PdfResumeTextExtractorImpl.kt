package com.glinboy.opportune.service.impl

import com.glinboy.opportune.service.ResumeTextExtractor
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class PdfResumeTextExtractorImpl : ResumeTextExtractor {

    companion object {
        private val SUPPORTED_TYPES = setOf(
            "application/pdf",
            "application/octet-stream"
        )
    }

    override fun supports(contentType: String): Boolean =
        SUPPORTED_TYPES.any { contentType.startsWith(it) }

    override fun extractText(inputStream: InputStream): String {
        val bytes = inputStream.readBytes()
        return Loader.loadPDF(bytes).use { doc ->
            PDFTextStripper().getText(doc)
        }
    }
}
