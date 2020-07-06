package com.oneightwo.scholarship_distribution_v2.distribution_service.service

import com.oneightwo.scholarship_distribution_v2.models.ScienceDirectionDTO
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO
import com.oneightwo.scholarship_distribution_v2.models.UniversityDTO

interface DataService {

    fun getScienceDirections(): List<ScienceDirectionDTO>

    fun getUniversities(): List<UniversityDTO>

    fun getStudentByMonthAndYear(): List<StudentDTO>
}