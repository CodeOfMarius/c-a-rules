package de.adesso.archunit.application.service

import de.adesso.archunit.application.port.`in`.usecase.CreateUserUseCase
import de.adesso.archunit.application.port.out.CreateUserPort
import de.adesso.archunit.application.port.out.SendEmailNotificationPort
import de.adesso.archunit.domain.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
internal class CreateUserService(
    private val createUserPort: CreateUserPort,
    private val sendEmailNotificationPort: SendEmailNotificationPort
) : CreateUserUseCase {

    private val logger: Logger = LoggerFactory.getLogger(CreateUserService::class.java)
    override fun createUser(user: User): User {
        createUserPort.createUser(user)
        logger.info("User was created")
        sendEmailNotificationPort.sendEmailToUser(user)
        return user;
    }
}
