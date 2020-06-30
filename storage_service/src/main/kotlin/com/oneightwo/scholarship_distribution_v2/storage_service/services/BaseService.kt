package com.oneightwo.scholarship_distribution_v2.storage_service.services

interface BaseService<T, ID> {
    fun find(id: ID): T

    fun save(t: T): T

    fun update(t: T): T

    fun delete(id: ID)
}