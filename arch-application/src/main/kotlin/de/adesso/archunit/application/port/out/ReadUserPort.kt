package de.adesso.archunit.application.port.out

import de.adesso.archunit.domain.User

internal interface ReadUserPort {
    fun readUser(userId: String): User
}
