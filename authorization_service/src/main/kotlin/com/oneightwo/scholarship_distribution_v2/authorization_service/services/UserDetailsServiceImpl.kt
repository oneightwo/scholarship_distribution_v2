package com.oneightwo.scholarship_distribution_v2.authorization_service.services

import com.oneightwo.scholarship_distribution_v2.authorization_service.models.AuthUserDetails
import com.oneightwo.scholarship_distribution_v2.authorization_service.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service("userDetailsService")
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String): UserDetails {
        val userDetails = AuthUserDetails(userRepository.findByUsername(username)
                ?: throw UsernameNotFoundException("Username or password wrong"))
        AccountStatusUserDetailsChecker().check(userDetails)
        return userDetails
    }
}