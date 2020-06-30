package com.oneightwo.scholarship_distribution_v2.storage_service.repositories

import com.oneightwo.scholarship_distribution_v2.storage_service.models.Student
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<Student, Long>, PagingAndSortingRepository<Student, Long> {

    fun findByName(name: String): Student

    @Query(
        value = """SELECT * 
                   FROM students 
                   WHERE Extract(MONTH from data_registration) IN (:months)
                    AND Extract(YEAR from data_registration) IN (:year)""",
        nativeQuery = true)
    fun getStudentByMonthsAndYear(
        @Param("months") months: List<Int>,
        @Param("year") year: Int,
        pageable: Pageable
    ): List<Student>
}