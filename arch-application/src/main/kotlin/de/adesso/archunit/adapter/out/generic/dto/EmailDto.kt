package de.adesso.archunit.adapter.out.generic.dto

internal data class EmailDto(
    val email: String,
    val subject: String,
    val content: String
)