package com.oneightwo.scholarship_distribution_v2.storage_service.models

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "universities")
class University (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "university_id")
        var id: Long? = null,

        @Column(nullable = false)
        var name: String = "",

        @Column(nullable = false)
        var abbreviation: String = "",

        @Column(nullable = false)
        var deleted: Boolean = false
) : Serializable