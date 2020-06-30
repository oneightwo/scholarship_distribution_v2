package com.oneightwo.scholarship_distribution_v2.storage_service.controllers

import com.oneightwo.scholarship_distribution_v2.constants.COURSES_URL
import com.oneightwo.scholarship_distribution_v2.models.CourseDTO
import com.oneightwo.scholarship_distribution_v2.storage_service.helpers.TransformationHelper
import com.oneightwo.scholarship_distribution_v2.storage_service.services.CourseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [COURSES_URL])
class CourseController {

    @Autowired
    private lateinit var courseService: CourseService

    @PostMapping
    fun saveCourse(@RequestBody courseDTO: CourseDTO): ResponseEntity<Any> {
        return ResponseEntity.ok(
            TransformationHelper.entityToDto(
                courseService.save(
                    TransformationHelper.dtoToEntity(
                        courseDTO
                    )
                )
            )
        )
    }

    @GetMapping("/{id}")
    fun findCourse(@PathVariable id: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(TransformationHelper.entityToDto(courseService.find(id)))
    }

    @PutMapping("/{courseId}")
    fun updateCourse(@PathVariable courseId: Long, @RequestBody courseDTO: CourseDTO): ResponseEntity<Any> {
        return ResponseEntity.ok(
            TransformationHelper.entityToDto(
                courseService.update(
                    TransformationHelper.dtoToEntity(
                        courseDTO.apply { id = courseId })
                )
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteCourse(@PathVariable id: Long): ResponseEntity<Any> {
        courseService.delete(id)
        return ResponseEntity.ok().build()
    }
}