package de.adesso.archunit.adapter.`in`.internal

import de.adesso.archunit.application.port.`in`.query.ReadUserQuery
import de.adesso.archunit.domain.User
import de.adesso.archunit.port.`in`.internal.InternalReadUserPort
import de.adesso.archunit.port.`in`.internal.dto.UserDto
import org.springframework.stereotype.Service

@Service
internal class InternalReadUserAdapter(
    private val readUserQuery: ReadUserQuery
): InternalReadUserPort {
    override fun readUser(userId: String): UserDto {
        return toDto(readUserQuery.readUser(userId))
    }

    private fun toDto(user: User): UserDto {
        return UserDto(
            user.userId,
            user.username
        )
    }
}
