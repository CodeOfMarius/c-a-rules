package de.adesso.archunit.adapter.`in`.web

import de.adesso.archunit.adapter.`in`.web.dto.`in`.UserDtoIn
import de.adesso.archunit.adapter.`in`.web.dto.out.UserDtoOut
import de.adesso.archunit.application.port.`in`.usecase.CreateUserUseCase
import de.adesso.archunit.domain.User
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class UserController(private val createUserUseCase: CreateUserUseCase) {

    @Operation
    @GetMapping
    fun getUser(user: UserDtoIn) : UserDtoOut{
        return toDto(createUserUseCase.createUser(toUser(user)))
    }

    private fun toUser(user: UserDtoIn): User {
        return User(
            user.userId,
            user.username,
            user.email
        )
    }
    private fun toDto(user: User): UserDtoOut {
        return UserDtoOut(
            user.userId,
            user.username
        )
    }
}
