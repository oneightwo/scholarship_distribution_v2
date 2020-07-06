package com.oneightwo.scholarship_distribution_v2.storage_service.models

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "students")
class Student(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "student_id")
        var id: Long? = null,

        @Column(nullable = false)
        var surname: String = "",

        @Column(nullable = false)
        var name: String = "",

        var patronymic: String? = null,

        @JoinColumn(name = "university_id")
        @ManyToOne(cascade = [CascadeType.REFRESH])
        var university: University? = null,

        @Column(nullable = false)
        var faculty: String = "",

        @JoinColumn(name = "course_id")
        @ManyToOne(cascade = [CascadeType.REFRESH])
        var course: Course? = null,

        @Column(nullable = false)
        var email: String = "",

        var phone: String? = null,

        @JoinColumn(name = "science_direction_id")
        @ManyToOne(cascade = [CascadeType.REFRESH])
        var scienceDirection: ScienceDirection? = null,

        @Column(nullable = false)
        var topic: String = "",

        @Column(nullable = false)
        var c1: Int = 0,

        @Column(nullable = false)
        var c2: Int = 0,

        @Column(nullable = false)
        var c3: Int = 0,

        @Column(nullable = false)
        var c4: Int = 0,

        @Column(nullable = false)
        var c5: Int = 0,

        @Column(nullable = false)
        var c6: Int = 0,

        @Column(nullable = false)
        var c7: Int = 0,

        @Column(nullable = false)
        var c8: Int = 0,

        @Column(nullable = false)
        var c9: Int = 0,

        @Column(nullable = false)
        var c10: Int = 0,

        @Column(nullable = false)
        var c11: Int = 0,

        @Column(nullable = false)
        var c12: Int = 0,

        @Column(nullable = false)
        var c13: Int = 0,

        @Column(nullable = false)
        var c14: Int = 0,

        @Column(nullable = false)
        var c15: Int = 0,

        @Column(nullable = false)
        var rating: Float = 0F,

        @Column(nullable = false)
        var data_registration: LocalDateTime,

        @Column(nullable = false)
        var isValid: Boolean = false
) : Serializable