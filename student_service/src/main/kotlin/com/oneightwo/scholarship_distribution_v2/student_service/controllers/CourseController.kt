package com.oneightwo.scholarship_distribution_v2.student_service.controllers

import com.oneightwo.scholarship_distribution_v2.student_service.dto.CourseDTO
import com.oneightwo.scholarship_distribution_v2.student_service.helpers.TransformationHelper
import com.oneightwo.scholarship_distribution_v2.student_service.services.CourseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/v1/courses"])
class CourseController {

    @Autowired
    private lateinit var courseService: CourseService

    @PostMapping
    fun saveStudent(@RequestBody courseDTO: CourseDTO): CourseDTO {
        return TransformationHelper.entityToDto(courseService.save(TransformationHelper.dtoToEntity(courseDTO)))
    }

    @GetMapping("/{id}")
    fun findStudent(@PathVariable id: Long): CourseDTO {
        return TransformationHelper.entityToDto(courseService.find(id))
    }

    @PutMapping("/{courseId}")
    fun updateStudent(@PathVariable courseId: Long, @RequestBody courseDTO: CourseDTO): CourseDTO {
        return TransformationHelper.entityToDto(courseService.update(TransformationHelper.dtoToEntity(courseDTO.apply { id = courseId })))
    }

    @DeleteMapping("/{id}")
    fun deleteStudent(@PathVariable id: Long) {
        courseService.delete(id)
    }
}