package com.oneightwo.scholarship_distribution_v2.storage_service.services

import com.oneightwo.scholarship_distribution_v2.storage_service.models.ScienceDirection

interface ScienceDirectionService : BaseService<ScienceDirection, Long> {

    fun getAll(): List<ScienceDirection>
}