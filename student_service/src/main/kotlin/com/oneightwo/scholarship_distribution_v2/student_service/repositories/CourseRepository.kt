package com.oneightwo.scholarship_distribution_v2.student_service.repositories

import com.oneightwo.scholarship_distribution_v2.student_service.models.Course
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : JpaRepository<Course, Long> {
}