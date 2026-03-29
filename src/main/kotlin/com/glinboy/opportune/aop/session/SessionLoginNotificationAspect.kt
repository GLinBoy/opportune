package com.glinboy.opportune.aop.session

import com.glinboy.opportune.dto.SessionDTO
import com.glinboy.opportune.mapper.ProfileMapper
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.service.MailService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * AOP aspect that sends a login-notification email to the user
 * after a new session is successfully persisted.
 *
 * A call to [com.glinboy.opportune.service.SessionService.save] is considered
 * a **new login** when the incoming [SessionDTO.lastRefreshedAt] is `null`.
 * Token-refresh flows always pass a non-null [SessionDTO.lastRefreshedAt],
 * so those are silently ignored.
 */
@Aspect
@Component
@Suppress("unused")
class SessionLoginNotificationAspect(
	private val mailService: MailService,
	private val profileRepository: ProfileRepository,
	private val profileMapper: ProfileMapper
) {

	private val log = LoggerFactory.getLogger(this::class.java)

	/**
	 * Pointcut targeting [com.glinboy.opportune.service.SessionService.save].
	 */
	@Pointcut("execution(* com.glinboy.opportune.service.SessionService.save(..))")
	fun sessionSave() = Unit

	/**
	 * After the session is successfully saved, send a login notification email
	 * if this represents a brand-new login (not a token refresh).
	 *
	 * @param joinPoint  the intercepted join point – used to access the original argument
	 * @param savedSession  the [SessionDTO] returned by the save operation
	 */
	@AfterReturning(pointcut = "sessionSave()", returning = "savedSession")
	fun onSessionSaved(joinPoint: JoinPoint, savedSession: Any?) {
		val inputSession = joinPoint.args.firstOrNull() as? SessionDTO ?: return
		val session = savedSession as? SessionDTO ?: return

		// Only fire for new logins; token-refresh calls always carry a non-null lastRefreshedAt
		if (inputSession.lastRefreshedAt != null) return

		val profileId = session.profileId ?: run {
			log.warn("SessionLoginNotificationAspect: sessionDTO has no profileId, skipping email")
			return
		}

		val profile = profileRepository.findById(profileId).map(profileMapper::toDto).orElse(null) ?: run {
			log.warn("SessionLoginNotificationAspect: profile {} not found, skipping email", profileId)
			return
		}

		try {
			mailService.sendLoginNotificationEmail(profile, session)
			log.debug("Login notification email dispatched for profile {}", profileId)
		} catch (ex: Exception) {
			log.warn("Failed to send login notification email for profile {}", profileId, ex)
		}
	}
}


