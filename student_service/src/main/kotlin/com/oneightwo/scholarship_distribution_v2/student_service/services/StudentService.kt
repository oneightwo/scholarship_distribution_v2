package com.oneightwo.scholarship_distribution_v2.student_service.services

import com.oneightwo.scholarship_distribution_v2.student_service.models.Student

interface StudentService {

    fun find(id: Long): Student

    fun save(student: Student): Student

    fun update(student: Student): Student

    fun delete(id: Long)
}