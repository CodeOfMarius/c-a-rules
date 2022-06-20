package de.adesso.archunit.adapter.out.database

import de.adesso.archunit.adapter.out.database.entities.UserEntity
import de.adesso.archunit.adapter.out.database.repositories.UserRepository
import de.adesso.archunit.application.port.out.ReadUserPort
import de.adesso.archunit.domain.User
import org.springframework.stereotype.Service

@Service
internal class ReadUserAdapter(
    private val userRepository: UserRepository
) : ReadUserPort {

    override fun readUser(userId: String): User {
        val user = userRepository.findById(userId).orElseThrow{ IllegalArgumentException() }
        return entityToUser(user)
    }

    private fun entityToUser(user: UserEntity): User {
        return User(
            user.id,
            user.name,
            user.email
        )
    }
}
