package de.adesso.archunit.adapter.`in`.web.dto.out

import io.swagger.v3.oas.annotations.media.Schema

internal data class UserDtoOut(
    @field:Schema
    val userId: String,
    @field:Schema
    val username: String
)
