package de.adesso.archunit.adapter.out.generic

import de.adesso.archunit.adapter.out.generic.dto.EmailDto
import de.adesso.archunit.application.port.out.SendEmailNotificationPort
import de.adesso.archunit.domain.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
internal class SendEmailNotificationAdapter : SendEmailNotificationPort {

    private val logger: Logger = LoggerFactory.getLogger(SendEmailNotificationPort::class.java)

    override fun sendEmailToUser(user: User): Boolean {
        val email = createEmailToUser(user)
        //Implement Technical Details to send E-Mails
        return true;
    }

    private fun createEmailToUser(user: User): EmailDto {
        return EmailDto(
            user.email,
            "Test Subjekt",
            "Test Content"
        )
    }
}