package com.oneightwo.scholarship_distribution_v2.models

import java.io.Serializable

class UniversityDTO(
    var id: Long? = null,

    var name: String = "",

    var abbreviation: String = "",

    var deleted: Boolean = false
) : Serializable {
}