package com.oneightwo.scholarship_distribution_v2.authorization_service.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthUserDetails(private val user: User) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val result = arrayListOf<GrantedAuthority>()
        user.roles!!.forEach { r ->
            result.add(SimpleGrantedAuthority(r.name!!))
            r.permissions!!.forEach { p -> result.add(SimpleGrantedAuthority(p.name!!)) }
        }
        return result
    }

    override fun isEnabled() = user.enabled!!

    override fun getUsername() = user.username!!

    override fun isCredentialsNonExpired() = user.credentialsNonExpired!!

    override fun getPassword() = user.password!!

    override fun isAccountNonExpired() = user.accountNonExpired!!

    override fun isAccountNonLocked() = user.accountNonLocked!!
}