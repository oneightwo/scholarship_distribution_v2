package com.oneightwo.scholarship_distribution_v2.storage_service.controllers

import com.oneightwo.scholarship_distribution_v2.constants.UNIVERSITIES_URL
import com.oneightwo.scholarship_distribution_v2.models.UniversityDTO
import com.oneightwo.scholarship_distribution_v2.storage_service.helpers.TransformationHelper
import com.oneightwo.scholarship_distribution_v2.storage_service.services.UniversityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [UNIVERSITIES_URL])
class UniversityController {

    @Autowired
    private lateinit var universityService: UniversityService

    //---Base---

    @PostMapping
    fun saveUniversity(@RequestBody universityDTO: UniversityDTO): ResponseEntity<Any> {
        return ResponseEntity.ok(
            TransformationHelper.entityToDto(
                universityService.save(
                    TransformationHelper.dtoToEntity(
                        universityDTO
                    )
                )
            )
        )
    }

    @GetMapping("/{id}")
    fun findUniversity(@PathVariable id: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(TransformationHelper.entityToDto(universityService.find(id)))
    }

    @PutMapping("/{universityId}")
    fun updateUniversity(
        @PathVariable universityId: Long,
        @RequestBody universityDTO: UniversityDTO
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(
            TransformationHelper.entityToDto(
                universityService.update(
                    TransformationHelper.dtoToEntity(
                        universityDTO.apply { id = universityId })
                )
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteUniversity(@PathVariable id: Long): ResponseEntity<Any> {
        universityService.delete(id)
        return ResponseEntity.ok().build()
    }


    //---Additional---

    @GetMapping
    fun getUniversities(): ResponseEntity<Any> {
        return ResponseEntity.ok(universityService.getAll().map { TransformationHelper.entityToDto(it) })
    }

}