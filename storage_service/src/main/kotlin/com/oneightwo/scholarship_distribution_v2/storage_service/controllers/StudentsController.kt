package com.oneightwo.scholarship_distribution_v2.storage_service.controllers

import com.oneightwo.scholarship_distribution_v2.constants.STUDENTS_URL
import com.oneightwo.scholarship_distribution_v2.constants.Semester
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO
import com.oneightwo.scholarship_distribution_v2.storage_service.services.StudentService
import com.oneightwo.scholarship_distribution_v2.storage_service.helpers.TransformationHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [STUDENTS_URL])
class StudentsController {

    @Autowired
    private lateinit var studentService: StudentService

    @PostMapping
    fun saveStudent(@RequestBody studentDTO: StudentDTO): ResponseEntity<Any> {
        return ResponseEntity.ok(
            TransformationHelper.entityToDto(
                studentService.save(
                    TransformationHelper.dtoToEntity(
                        studentDTO
                    )
                )
            )
        )
    }

    @GetMapping("/{id}")
    fun findStudent(@PathVariable id: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(TransformationHelper.entityToDto(studentService.find(id)))
    }

    @PutMapping("/{studentId}")
    fun updateStudent(@PathVariable studentId: Long, @RequestBody studentDTO: StudentDTO): ResponseEntity<Any> {
        return ResponseEntity.ok(
            TransformationHelper.entityToDto(
                studentService.update(
                    TransformationHelper.dtoToEntity(
                        studentDTO.apply {
                            id = studentId
                        })
                )
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteStudent(@PathVariable id: Long): ResponseEntity<Any> {
        studentService.delete(id)
        return ResponseEntity.ok().build()
    }

    //---Additional---

//    @GetMapping
//    private fun getStudents(
//        @RequestParam year: Int,
//        @RequestParam semester: String,
//        @PageableDefault(page = 0, size = 100) //ОШИБКА: столбец "ddf" не существует
//        @SortDefault(sort = ["surname"], direction = Sort.Direction.ASC) pageable: Pageable
//    ): ResponseEntity<Any> {
//        return ResponseEntity.ok(studentService.getStudentBySemesterAndYear(
//            Semester.getSemesterByName(semester),
//            year,
//            pageable
//        )
//            .map { TransformationHelper.entityToDto(it) })
//    }

    @GetMapping
    private fun getStudentsf(
        @PageableDefault(page = 0, size = 100) //ОШИБКА: столбец "ddf" не существует
        @SortDefault(sort = ["surname"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(studentService.getStudentBySemesterAndYear(
            Semester.getSemesterByName(Semester.AUTUMN.name),
            2020,
            pageable
        )
            .map { TransformationHelper.entityToDto(it) })
    }
}