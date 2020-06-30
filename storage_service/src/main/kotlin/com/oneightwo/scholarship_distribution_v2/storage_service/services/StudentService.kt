package com.oneightwo.scholarship_distribution_v2.storage_service.services

import com.oneightwo.scholarship_distribution_v2.constants.Semester
import com.oneightwo.scholarship_distribution_v2.storage_service.models.Student
import org.springframework.data.domain.Pageable

interface StudentService : BaseService<Student, Long> {

    fun getStudentBySemesterAndYear(semester: Semester, year: Int, pageable: Pageable): List<Student>
}