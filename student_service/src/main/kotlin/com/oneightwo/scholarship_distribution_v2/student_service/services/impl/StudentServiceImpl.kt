package com.oneightwo.scholarship_distribution_v2.student_service.services.impl

import com.oneightwo.scholarship_distribution_v2.student_service.core.exceptions.ObjectNotFoundException
import com.oneightwo.scholarship_distribution_v2.student_service.models.Student
import com.oneightwo.scholarship_distribution_v2.student_service.repositories.StudentRepository
import com.oneightwo.scholarship_distribution_v2.student_service.services.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StudentServiceImpl : StudentService {

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Transactional(readOnly = true)
    override fun find(id: Long): Student = studentRepository.findById(id).orElseThrow { ObjectNotFoundException("Student not found") }

    override fun save(t: Student): Student = studentRepository.save(t)

    override fun update(t: Student): Student {
        find(t.id ?: throw ObjectNotFoundException("Student not found"))
        return studentRepository.save(t)
    }

    override fun delete(id: Long) = studentRepository.deleteById(id)
}