package com.oneightwo.scholarship_distribution_v2.student_service.controllers

import com.oneightwo.scholarship_distribution_v2.student_service.dto.StudentDTO
import com.oneightwo.scholarship_distribution_v2.student_service.services.StudentService
import com.oneightwo.scholarship_distribution_v2.student_service.helpers.TransformationHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/v1/students"])
class StudentsController {

    @Autowired
    private lateinit var studentService: StudentService

    @PostMapping
    fun saveStudent(@RequestBody studentDTO: StudentDTO): StudentDTO {
        return TransformationHelper.entityToDto(studentService.save(TransformationHelper.dtoToEntity(studentDTO)))
    }

    @GetMapping("/{id}")
    fun findStudent(@PathVariable id: Long): StudentDTO {
        return TransformationHelper.entityToDto(studentService.find(id))
    }

    @PutMapping("/{studentId}")
    fun updateStudent(@PathVariable studentId: Long, @RequestBody studentDTO: StudentDTO): StudentDTO {
        return TransformationHelper.entityToDto(studentService.update(TransformationHelper.dtoToEntity(studentDTO.apply { id = studentId })))
    }

    @DeleteMapping("/{id}")
    fun deleteStudent(@PathVariable id: Long) {
       studentService.delete(id)
    }
}