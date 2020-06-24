package com.oneightwo.scholarship_distribution_v2.student_service.services.impl

import com.oneightwo.scholarship_distribution_v2.student_service.core.exceptions.ObjectNotFoundException
import com.oneightwo.scholarship_distribution_v2.student_service.models.Student
import com.oneightwo.scholarship_distribution_v2.student_service.repositories.StudentRepository
import com.oneightwo.scholarship_distribution_v2.student_service.services.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StudentServiceImpl : StudentService {

    @Autowired
    private lateinit var studentRepository: StudentRepository

    override fun find(id: Long): Student = studentRepository.findById(id).orElseThrow { ObjectNotFoundException("Student not found") }

    override fun save(student: Student) = studentRepository.save(student)

    override fun update(student: Student): Student {
        find(student.id ?: throw ObjectNotFoundException("Student not found"))
        return studentRepository.save(student)
    }

    override fun delete(id: Long) = studentRepository.deleteById(id)
}