package de.adesso.archunit.application.port.out

import de.adesso.archunit.domain.User

internal interface SendEmailNotificationPort {
    fun sendEmailToUser(user: User): Boolean
}
