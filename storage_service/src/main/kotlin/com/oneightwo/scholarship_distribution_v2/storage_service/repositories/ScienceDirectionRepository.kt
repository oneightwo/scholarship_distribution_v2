package com.oneightwo.scholarship_distribution_v2.storage_service.repositories

import com.oneightwo.scholarship_distribution_v2.storage_service.models.ScienceDirection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScienceDirectionRepository : JpaRepository<ScienceDirection, Long> {

    fun getAllByDeletedFalse(): List<ScienceDirection>
}