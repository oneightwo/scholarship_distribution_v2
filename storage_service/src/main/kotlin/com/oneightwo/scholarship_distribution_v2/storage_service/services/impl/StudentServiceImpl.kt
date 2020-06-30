package com.oneightwo.scholarship_distribution_v2.storage_service.services.impl

import com.oneightwo.scholarship_distribution_v2.constants.Semester
import com.oneightwo.scholarship_distribution_v2.storage_service.core.exceptions.ObjectNotFoundException
import com.oneightwo.scholarship_distribution_v2.storage_service.models.Student
import com.oneightwo.scholarship_distribution_v2.storage_service.repositories.StudentRepository
import com.oneightwo.scholarship_distribution_v2.storage_service.services.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StudentServiceImpl : StudentService {

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Transactional(readOnly = true)
    override fun find(id: Long): Student =
        studentRepository.findById(id).orElseThrow { ObjectNotFoundException("Student not found") }

    override fun save(t: Student): Student = studentRepository.save(t)

    override fun update(t: Student): Student {
        find(t.id ?: throw ObjectNotFoundException("Student not found"))
        return studentRepository.save(t)
    }

    override fun delete(id: Long) = studentRepository.deleteById(id)

    override fun getStudentBySemesterAndYear(semester: Semester, year: Int, pageable: Pageable): List<Student> =
        studentRepository.getStudentByMonthsAndYear(semester.semesters, year, pageable)

}