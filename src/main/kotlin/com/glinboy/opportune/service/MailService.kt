package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.dto.SessionDTO

interface MailService {
	fun sendEmail(to: String, subject: String, content: String, isMultipart: Boolean, isHtml: Boolean)
	fun sendActivationEmail(profileDTO: ProfileDTO, code: String)
	fun sendPasswordResetMail(profileDTO: ProfileDTO, code: String)
	fun sendLoginNotificationEmail(profileDTO: ProfileDTO, sessionDTO: SessionDTO)
	fun sendPasswordResetSuccessEmail(profileDTO: ProfileDTO)
}
