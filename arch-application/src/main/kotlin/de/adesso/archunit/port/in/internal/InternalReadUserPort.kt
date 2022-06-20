package de.adesso.archunit.port.`in`.internal

import de.adesso.archunit.port.`in`.internal.dto.UserDto

interface InternalReadUserPort {
    fun readUser(userId: String): UserDto
}
