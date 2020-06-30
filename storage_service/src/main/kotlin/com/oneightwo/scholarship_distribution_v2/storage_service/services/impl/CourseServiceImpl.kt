package com.oneightwo.scholarship_distribution_v2.storage_service.services.impl

import com.oneightwo.scholarship_distribution_v2.storage_service.core.exceptions.ObjectNotFoundException
import com.oneightwo.scholarship_distribution_v2.storage_service.models.Course
import com.oneightwo.scholarship_distribution_v2.storage_service.repositories.CourseRepository
import com.oneightwo.scholarship_distribution_v2.storage_service.services.CourseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CourseServiceImpl : CourseService {

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Transactional(readOnly = true)
    override fun find(id: Long): Course = courseRepository.findById(id).orElseThrow { ObjectNotFoundException("Course not found") }

    override fun save(t: Course): Course = courseRepository.save(t)

    override fun update(t: Course): Course {
        find(t.id ?: throw ObjectNotFoundException("Course not found"))
        return courseRepository.save(t)
    }

    override fun delete(id: Long) {
        courseRepository.deleteById(id)
    }
}