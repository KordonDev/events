package de.kordondev.attendee.core.mail

import de.kordondev.attendee.core.security.AuthorityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.mail.SendFailedException



@Service("MailService")
class MailService (
        @Value("\${application.mail.send}") private val sendMail: Boolean,
        @Value("\${spring.mail.from}") private val sendFrom: String,
        @Value("\${data.kreiszeltlager.hostCity}") private val hostCity: String,
        @Value("\${data.kreiszeltlager.registrationDeadline}") private val registrationDeadline: String,
        private val authorityService: AuthorityService
) {

    @Autowired
    private lateinit var mailSender: JavaMailSender
    @Autowired
    private lateinit var htmlTemplateEngine: TemplateEngine
    private val logger: Logger = LoggerFactory.getLogger(MailService::class.java)
    private val newUserMailTemplate = "new-user"

    fun sendRegistrationMail(to: String, leaderName: String, username: String, password: String) {
        try {
            authorityService.isAdmin()
            val headerLogoName = "kreiszeltlager-logo.jpg"
            val headerLogo = ResourceUtils.getFile("classpath:static/$headerLogoName")

            val cxt = Context()
            val registrationDeadlineDate = LocalDate.parse(registrationDeadline, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            cxt.setVariable("leaderName", leaderName)
            cxt.setVariable("hostCity", hostCity)
            cxt.setVariable("registrationDeadline", registrationDeadlineDate)
            cxt.setVariable("username", username)
            cxt.setVariable("password", password)
            cxt.setVariable("headerLogo", headerLogoName);

            val mimeMessage = this.mailSender.createMimeMessage()
            val message = MimeMessageHelper(mimeMessage, true, "UTF-8")
            message.setFrom(sendFrom)
            message.setSubject("Onlineanmeldung Kreiszeltlager in $hostCity eröffnet")
            message.setTo(to)
            val htmlContent = this.htmlTemplateEngine.process(newUserMailTemplate, cxt)
            message.setText(htmlContent, true)
            message.addInline(headerLogoName, headerLogo);

            if (sendMail) {
                logger.info("RegistrationMail send to $to")
                this.mailSender.send(mimeMessage)
            } else {
                logger.info("New user mail $message", message)
            }
        } catch (exception:SendFailedException) {
            logger.error(exception.message)
        }
    }
}