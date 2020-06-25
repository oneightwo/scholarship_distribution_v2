package com.oneightwo.scholarship_distribution_v2.student_service.controllers

import com.oneightwo.scholarship_distribution_v2.student_service.dto.UniversityDTO
import com.oneightwo.scholarship_distribution_v2.student_service.helpers.TransformationHelper
import com.oneightwo.scholarship_distribution_v2.student_service.services.UniversityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/v1/universities"])
class UniversityController {

    @Autowired
    private lateinit var universityService: UniversityService

    @PostMapping
    fun saveStudent(@RequestBody universityDTO: UniversityDTO): UniversityDTO {
        return TransformationHelper.entityToDto(universityService.save(TransformationHelper.dtoToEntity(universityDTO)))
    }

    @GetMapping("/{id}")
    fun findStudent(@PathVariable id: Long): UniversityDTO {
        return TransformationHelper.entityToDto(universityService.find(id))
    }

    @PutMapping("/{universityId}")
    fun updateStudent(@PathVariable universityId: Long, @RequestBody universityDTO: UniversityDTO): UniversityDTO {
        return TransformationHelper.entityToDto(universityService.update(TransformationHelper.dtoToEntity(universityDTO.apply { id = universityId })))
    }

    @DeleteMapping("/{id}")
    fun deleteStudent(@PathVariable id: Long) {
        universityService.delete(id)
    }
}