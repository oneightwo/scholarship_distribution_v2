package com.oneightwo.scholarship_distribution_v2.storage_service.services

import com.oneightwo.scholarship_distribution_v2.storage_service.models.University

interface UniversityService : BaseService<University, Long> {

    fun getAll(): List<University>
}