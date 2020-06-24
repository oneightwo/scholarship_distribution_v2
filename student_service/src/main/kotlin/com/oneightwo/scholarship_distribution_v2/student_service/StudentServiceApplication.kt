package com.oneightwo.scholarship_distribution_v2.student_service

import com.oneightwo.scholarship_distribution_v2.student_service.services.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StudentServiceApplication

fun main(args: Array<String>) {
    runApplication<StudentServiceApplication>(*args)
}
