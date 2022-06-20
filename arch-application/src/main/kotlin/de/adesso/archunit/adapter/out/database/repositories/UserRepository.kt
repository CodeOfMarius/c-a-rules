package de.adesso.archunit.adapter.out.database.repositories

import de.adesso.archunit.adapter.out.database.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface UserRepository : JpaRepository<UserEntity, String> {
}
