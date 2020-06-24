package com.oneightwo.scholarship_distribution_v2.student_service.models

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "courses")
data class Course(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "student_id")
        var id: Long? = null,

        @Column(nullable = false)
        var name: Int = 0
) : Serializable