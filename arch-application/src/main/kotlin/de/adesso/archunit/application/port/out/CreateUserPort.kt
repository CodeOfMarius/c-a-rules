package de.adesso.archunit.application.port.out

import de.adesso.archunit.domain.User

internal interface CreateUserPort {
    fun createUser(user: User): User
}
