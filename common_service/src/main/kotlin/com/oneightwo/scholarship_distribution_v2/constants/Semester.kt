package com.oneightwo.scholarship_distribution_v2.constants

import com.oneightwo.scholarship_distribution_v2.exceptions.SemesterNotFoundException

enum class Semester(val semesters: List<Int>) {

    AUTUMN(listOf(9, 10, 11, 12)),
    SPRING(listOf(1, 2, 3, 4, 5, 6, 7, 8));

    companion object {
        @Throws(SemesterNotFoundException::class)
        fun getSemesterByName(semester: String): Semester {
            try {
                return valueOf(semester.toUpperCase())
            } catch (e: Exception) {
                throw SemesterNotFoundException()
            }
        }
    }
}