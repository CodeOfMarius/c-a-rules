package de.adesso.archunit.adapter.out.database

import de.adesso.archunit.adapter.out.database.entities.UserEntity
import de.adesso.archunit.adapter.out.database.repositories.UserRepository
import de.adesso.archunit.application.port.out.CreateUserPort
import de.adesso.archunit.domain.User
import org.springframework.stereotype.Service

@Service
internal class CreateUserAdapter(
    private val userRepository: UserRepository
) : CreateUserPort {

    override fun createUser(user: User): User {
        return entityToUser(userRepository.save(userToEntity(user)))
    }

    private fun userToEntity(user: User): UserEntity {
        return UserEntity(
            user.userId,
            user.username,
            user.email
        )
    }
    private fun entityToUser(user: UserEntity): User {
        return User(
            user.id,
            user.name,
            user.email
        )
    }
}
