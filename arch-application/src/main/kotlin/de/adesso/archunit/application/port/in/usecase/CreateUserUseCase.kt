package de.adesso.archunit.application.port.`in`.usecase

import de.adesso.archunit.domain.User

internal interface CreateUserUseCase {
    fun createUser(user: User): User
}
