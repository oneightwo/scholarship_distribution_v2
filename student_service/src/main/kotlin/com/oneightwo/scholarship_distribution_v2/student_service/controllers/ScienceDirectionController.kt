package com.oneightwo.scholarship_distribution_v2.student_service.controllers

import com.oneightwo.scholarship_distribution_v2.student_service.dto.ScienceDirectionDTO
import com.oneightwo.scholarship_distribution_v2.student_service.helpers.TransformationHelper
import com.oneightwo.scholarship_distribution_v2.student_service.services.ScienceDirectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/v1/sciencedirection"])
class ScienceDirectionController {

    @Autowired
    private lateinit var scienceDirectionService: ScienceDirectionService

    @PostMapping
    fun saveStudent(@RequestBody scienceDirectionDTO: ScienceDirectionDTO): ScienceDirectionDTO {
        return TransformationHelper.entityToDto(scienceDirectionService.save(TransformationHelper.dtoToEntity(scienceDirectionDTO)))
    }

    @GetMapping("/{id}")
    fun findStudent(@PathVariable id: Long): ScienceDirectionDTO {
        return TransformationHelper.entityToDto(scienceDirectionService.find(id))
    }

    @PutMapping("/{directionId}")
    fun updateStudent(@PathVariable directionId: Long, @RequestBody scienceDirectionDTO: ScienceDirectionDTO): ScienceDirectionDTO {
        return TransformationHelper.entityToDto(scienceDirectionService.update(TransformationHelper.dtoToEntity(scienceDirectionDTO.apply { id = directionId })))
    }

    @DeleteMapping("/{id}")
    fun deleteStudent(@PathVariable id: Long) {
        scienceDirectionService.delete(id)
    }
}