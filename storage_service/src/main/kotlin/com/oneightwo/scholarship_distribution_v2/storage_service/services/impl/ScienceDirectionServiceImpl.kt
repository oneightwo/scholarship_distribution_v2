package com.oneightwo.scholarship_distribution_v2.storage_service.services.impl

import com.oneightwo.scholarship_distribution_v2.storage_service.core.exceptions.ObjectNotFoundException
import com.oneightwo.scholarship_distribution_v2.storage_service.models.ScienceDirection
import com.oneightwo.scholarship_distribution_v2.storage_service.repositories.ScienceDirectionRepository
import com.oneightwo.scholarship_distribution_v2.storage_service.services.ScienceDirectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ScienceDirectionServiceImpl : ScienceDirectionService {

    @Autowired
    private lateinit var scienceDirectionRepository: ScienceDirectionRepository

    @Transactional(readOnly = true)
    override fun find(id: Long): ScienceDirection {
        return scienceDirectionRepository.findById(id).orElseThrow { ObjectNotFoundException("ScienceDirection not found") }
    }

    override fun save(t: ScienceDirection): ScienceDirection = scienceDirectionRepository.save(t)

    override fun update(t: ScienceDirection): ScienceDirection {
        find(t.id ?: throw ObjectNotFoundException("ScienceDirection not found"))
        return scienceDirectionRepository.save(t)
    }

    override fun delete(id: Long) {
        update(find(id).also { it.deleted = true })
    }

    override fun getAll() = scienceDirectionRepository.getAllByDeletedFalse()

}