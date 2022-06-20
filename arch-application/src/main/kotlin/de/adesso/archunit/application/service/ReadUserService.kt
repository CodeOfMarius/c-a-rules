package de.adesso.archunit.application.service

import de.adesso.archunit.adapter.out.database.entities.UserEntity
import de.adesso.archunit.application.port.`in`.query.ReadUserQuery
import de.adesso.archunit.application.port.`in`.usecase.CreateUserUseCase
import de.adesso.archunit.application.port.out.CreateUserPort
import de.adesso.archunit.application.port.out.ReadUserPort
import de.adesso.archunit.domain.User
import org.springframework.stereotype.Service

@Service
internal class ReadUserService(
    private val readUserPort: ReadUserPort
) : ReadUserQuery {

    override fun readUser(userId: String): User {
        return readUserPort.readUser(userId)
    }
}
