package com.oneightwo.scholarship_distribution_v2.authorization_service.models

import java.io.Serializable
import javax.persistence.*


@Entity
@Table(name = "roles")
data class Role(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "role_id")
        val id: Long? = null,
        val name: String? = null,
        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "permissions_roles",
                joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "role_id")],
                inverseJoinColumns = [JoinColumn(name = "permission_id", referencedColumnName = "permission_id")])
        val permissions: List<Permission>? = null
) : Serializable