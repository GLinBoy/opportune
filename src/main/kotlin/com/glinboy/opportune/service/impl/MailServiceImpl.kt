package com.glinboy.opportune.service.impl

import com.glinboy.opportune.config.ApplicationProperties
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.dto.SessionDTO
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

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
		sendEmailFromTemplateSync(profileDTO, code, "mail/activation")
	}

	@Async
	override fun sendPasswordResetMail(profileDTO: ProfileDTO, code: String) {
		sendEmailFromTemplateSync(profileDTO, code, "mail/passwordReset")
	}

	@Async
	override fun sendPasswordResetSuccessEmail(profileDTO: ProfileDTO) {
		val locale = Locale.forLanguageTag("en")
		val ctx = Context(locale)
		ctx.setVariable("profile", profileDTO)
		ctx.setVariable("baseUrl", properties.info.website)
		val content = templateEngine.process("mail/passwordChanged", ctx)
		sendEmailSync(profileDTO.email!!, "Opportune - Password Reset Successful", content, false, true)
	}

	@Async
	override fun sendWelcomeEmail(profileDTO: ProfileDTO) {
		val locale = Locale.forLanguageTag("en")
		val ctx = Context(locale)
		ctx.setVariable("profile", profileDTO)
		ctx.setVariable("baseUrl", properties.info.website)
		val content = templateEngine.process("mail/welcome", ctx)
		sendEmailSync(profileDTO.email!!, "Welcome to Opportune!", content, false, true)
	}

	@Async
	override fun sendLoginNotificationEmail(profileDTO: ProfileDTO, sessionDTO: SessionDTO) {
		val locale = Locale.forLanguageTag("en")
		val ctx = Context(locale)
		ctx.setVariable("profile", profileDTO)
		ctx.setVariable("session", sessionDTO)
		ctx.setVariable("baseUrl", properties.info.website)
		val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'").withZone(ZoneId.of("UTC"))
		ctx.setVariable("loginAt", sessionDTO.loginAt?.let(formatter::format) ?: "-")
		val content = templateEngine.process("mail/login", ctx)
		sendEmailSync(profileDTO.email!!, "Opportune - New Login Detected", content, false, true)
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
			message.setFrom(properties.mail.from!!)
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
