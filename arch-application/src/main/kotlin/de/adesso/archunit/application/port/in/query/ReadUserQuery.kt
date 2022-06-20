package de.adesso.archunit.application.port.`in`.query

import de.adesso.archunit.domain.User

internal interface ReadUserQuery {
    fun readUser(userId: String): User
}
