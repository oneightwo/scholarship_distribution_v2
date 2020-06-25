package com.oneightwo.scholarship_distribution_v2.student_service.dto

import java.io.Serializable

class UniversityDTO(
    var id: Long? = null,

    var name: String = "",

    var abbreviation: String = ""
) : Serializable {
}