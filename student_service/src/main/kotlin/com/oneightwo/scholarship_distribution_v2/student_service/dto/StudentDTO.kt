package com.oneightwo.scholarship_distribution_v2.student_service.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.oneightwo.scholarship_distribution_v2.student_service.models.Student
import java.io.Serializable

class StudentDTO(
    var id: Long? = null,

    var surname: String = "",

    var name: String = "",

    var patronymic: String? = null,

    @JsonProperty("university_id")
    var universityId: Long? = null,

    var faculty: String = "",

    @JsonProperty("course_id")
    var courseId: Long? = null,

    var email: String = "",

    var phone: String? = null,

    @JsonProperty("science_direction_id")
    var scienceDirectionId: Long? = null,

    var topic: String = "",

    var c1: Int = 0,

    var c2: Int = 0,

    var c3: Int = 0,

    var c4: Int = 0,

    var c5: Int = 0,

    var c6: Int = 0,

    var c7: Int = 0,

    var c8: Int = 0,

    var c9: Int = 0,

    var c10: Int = 0,

    var c11: Int = 0,

    var c12: Int = 0,

    var c13: Int = 0,

    var c14: Int = 0,

    var c15: Int = 0,

    var rating: Float = 0F,

    var isValid: Boolean = false
) : Serializable