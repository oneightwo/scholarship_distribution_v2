package com.oneightwo.scholarship_distribution_v2.storage_service.services.impl

import com.oneightwo.scholarship_distribution_v2.storage_service.core.exceptions.ObjectNotFoundException
import com.oneightwo.scholarship_distribution_v2.storage_service.models.University
import com.oneightwo.scholarship_distribution_v2.storage_service.repositories.UniversityRepository
import com.oneightwo.scholarship_distribution_v2.storage_service.services.UniversityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UniversityServiceImpl : UniversityService {

    @Autowired
    private lateinit var universityRepository: UniversityRepository

    @Transactional(readOnly = true)
    override fun find(id: Long): University {
        return universityRepository.findById(id).orElseThrow { ObjectNotFoundException("University not found") }
    }

    override fun save(t: University): University = universityRepository.save(t)

    override fun update(t: University): University {
        find(t.id ?: throw ObjectNotFoundException("Student not found"))
        return universityRepository.save(t)
    }

    override fun delete(id: Long) {
        update(find(id).also { it.deleted = true })
    }

    override fun getAll() = universityRepository.getAllByDeletedFalse()
}