package com.oneightwo.scholarship_distribution_v2.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class StudentDTO(
    var id: Long? = null,

    val surname: String = "",

    val name: String = "",

    val patronymic: String? = null,

    @JsonProperty("university_id")
    val universityId: Long? = null,

    val faculty: String = "",

    @JsonProperty("course_id")
    val courseId: Long? = null,

    val email: String = "",

    val phone: String? = null,

    @JsonProperty("science_direction_id")
    val scienceDirectionId: Long? = null,

    val topic: String = "",

    val c1: Int = 0,

    val c2: Int = 0,

    val c3: Int = 0,

    val c4: Int = 0,

    val c5: Int = 0,

    val c6: Int = 0,

    val c7: Int = 0,

    val c8: Int = 0,

    val c9: Int = 0,

    val c10: Int = 0,

    val c11: Int = 0,

    val c12: Int = 0,

    val c13: Int = 0,

    val c14: Int = 0,

    val c15: Int = 0,

    var rating: Float = 0F,

    val isValid: Boolean = false
) : Serializable