package com.oneightwo.scholarship_distribution_v2.student_service.repositories

import com.oneightwo.scholarship_distribution_v2.student_service.models.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<Student, Long> {

    fun findByName(name: String): Student
}