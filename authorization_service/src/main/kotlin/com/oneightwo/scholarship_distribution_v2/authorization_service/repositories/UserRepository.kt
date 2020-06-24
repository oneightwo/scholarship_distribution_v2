package com.oneightwo.scholarship_distribution_v2.authorization_service.repositories

import com.oneightwo.scholarship_distribution_v2.authorization_service.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): User?
}