package com.oneightwo.scholarship_distribution_v2.distribution_service.service

import com.oneightwo.scholarship_distribution_v2.constants.Semester
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO

interface ReportService {

    fun getAllNumberApplicationsByUniversities(semester: Semester, year: Int): Map<String, Int>

    fun getPassedNumberApplicationsByUniversities(semester: Semester, year: Int): Map<String, Int>

    fun getReportByDirection(semester: Semester, year: Int): List<Map<String, String>>

    fun getReportByUniversitiesIntoDirections(semester: Semester, year: Int): Map<String, List<Map<String, String>>>

    fun getLoserStudents(semester: Semester, year: Int): Map<String, List<StudentDTO>>
}
