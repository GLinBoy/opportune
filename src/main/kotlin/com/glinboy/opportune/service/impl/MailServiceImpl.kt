package com.glinboy.opportune.service.impl

import com.glinboy.opportune.config.ApplicationProperties
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.service.MailService
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.nio.charset.StandardCharsets
import java.util.Locale

@Service
class MailServiceImpl(
	private val properties: ApplicationProperties,
	private val javaMailSender: JavaMailSender,
	private val templateEngine: SpringTemplateEngine
) : MailService {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	@Async
	override fun sendEmail(
		to: String,
		subject: String,
		content: String,
		isMultipart: Boolean,
		isHtml: Boolean
	) {
		sendEmailSync(to, subject, content, isMultipart, isHtml)
	}

	@Async
	override fun sendActivationEmail(profileDTO: ProfileDTO, code: String) {
		sendEmailFromTemplateSync(profileDTO, code, "mail/activationEmail")
	}

	@Async
	override fun sendPasswordResetMail(profileDTO: ProfileDTO, code: String) {
		sendEmailFromTemplateSync(profileDTO, code, "mail/passwordResetEmail")
	}

	private fun sendEmailSync(
		to: String,
		subject: String,
		content: String,
		isMultipart: Boolean,
		isHtml: Boolean
	) {
		log.debug(
			"Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
			isMultipart, isHtml, to, subject, content
		)

		val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
		try {
			val message = MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name())
			message.setTo(to)
			message.setFrom("info@opportune.app")
			message.setSubject(subject)
			message.setText(content, isHtml)
			javaMailSender.send(mimeMessage)
			log.debug("Sent email to User '{}'", to)
		} catch (e: MailException) {
			log.warn("Email could not be sent to user '{}'", to, e)
		} catch (e: MessagingException) {
			log.warn("Email could not be sent to user '{}'", to, e)
		}
	}

	private fun sendEmailFromTemplateSync(profileDTO: ProfileDTO, code: String, templateName: String) {
		val locale = Locale.forLanguageTag("en")
		val ctx = Context(locale)
		ctx.setVariable("profile", profileDTO)
		ctx.setVariable("code", code)
		ctx.setVariable("baseUrl", properties.info.website)
		val content = templateEngine.process(templateName, ctx)
		sendEmailSync(profileDTO.email!!, "Opportune - Email Verification", content, false, true)
	}
}
