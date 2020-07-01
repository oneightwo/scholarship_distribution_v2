package com.oneightwo.scholarship_distribution_v2.distribution_service.service

import com.oneightwo.scholarship_distribution_v2.models.ScienceDirectionDTO

interface DataService {

    fun getScienceDirections(): List<ScienceDirectionDTO>
}