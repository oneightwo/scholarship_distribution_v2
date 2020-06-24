package com.oneightwo.scholarship_distribution_v2.authorization_service.models

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "user_id")
        var id: Long? = null,
        var username: String = "",
        var password: String = "",
        var enabled: Boolean = true,
        @Column(name = "account_non_expired")
        var accountNonExpired: Boolean = true,
        @Column(name = "credentials_non_expired")
        var credentialsNonExpired: Boolean = true,
        @Column(name = "account_non_locked")
        var accountNonLocked: Boolean = true,
        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "roles_users",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "role_id")])
        var roles: List<Role> = listOf()
) : Serializable