package de.adesso.archunit.adapter.`in`.web.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema

internal data class UserDtoIn (
    @field:Schema
    val userId: String,
    @field:Schema
    val username: String,
    @field:Schema
    val email: String
)
