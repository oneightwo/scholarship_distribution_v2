package com.oneightwo.scholarship_distribution_v2.authorization_service.models

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "permissions")
class Permission(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "permission_id")
        val id: Long? = null,
        val name: String? = null
) : Serializable