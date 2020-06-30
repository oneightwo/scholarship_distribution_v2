package com.oneightwo.scholarship_distribution_v2.storage_service.controllers

import com.oneightwo.scholarship_distribution_v2.constants.SCIENCE_DIRECTIONS_URL
import com.oneightwo.scholarship_distribution_v2.models.ScienceDirectionDTO
import com.oneightwo.scholarship_distribution_v2.storage_service.helpers.TransformationHelper
import com.oneightwo.scholarship_distribution_v2.storage_service.services.ScienceDirectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [SCIENCE_DIRECTIONS_URL])
class ScienceDirectionController {

    @Autowired
    private lateinit var scienceDirectionService: ScienceDirectionService

    @PostMapping
    fun saveDirection(@RequestBody scienceDirectionDTO: ScienceDirectionDTO): ResponseEntity<Any> {
        return ResponseEntity.ok(
            TransformationHelper.entityToDto(
                scienceDirectionService.save(
                    TransformationHelper.dtoToEntity(
                        scienceDirectionDTO
                    )
                )
            )
        )
    }

    @GetMapping("/{id}")
    fun findDirection(@PathVariable id: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(TransformationHelper.entityToDto(scienceDirectionService.find(id)))
    }

    @PutMapping("/{directionId}")
    fun updateDirection(
        @PathVariable directionId: Long,
        @RequestBody scienceDirectionDTO: ScienceDirectionDTO
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(
            TransformationHelper.entityToDto(
                scienceDirectionService.update(
                    TransformationHelper.dtoToEntity(
                        scienceDirectionDTO.apply { id = directionId })
                )
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteDirection(@PathVariable id: Long): ResponseEntity<Any> {
        scienceDirectionService.delete(id)
        return ResponseEntity.ok().build()
    }

    //---Additional---

    @GetMapping
    fun getDirections(): ResponseEntity<Any> {
        return ResponseEntity.ok(scienceDirectionService.getAll().map { TransformationHelper.entityToDto(it) })
    }

}