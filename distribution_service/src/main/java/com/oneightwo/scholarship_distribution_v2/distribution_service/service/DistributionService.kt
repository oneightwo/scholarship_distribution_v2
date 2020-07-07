package com.oneightwo.scholarship_distribution_v2.distribution_service.service

import com.oneightwo.scholarship_distribution_v2.constants.Semester
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.*
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO
import org.springframework.beans.factory.annotation.Autowired
import kotlin.math.roundToInt

abstract class DistributionService {

    @Autowired
    protected lateinit var dataService: DataService

    protected fun calculationRating(criteria: IntArray): Int {
        var rating = 0.0
        criteria.indices.forEach { i ->
            rating += KEF[VK[i]] * UR[criteria[i]]
        }
        return rating.roundToInt()
    }

    fun execute(semester: Semester, year: Int, withoutCheck: Boolean = false): Map<Long, Map<Long, List<StudentDTO>>> {
        val students = dataService.getStudentByMonthAndYear(semester, year)
        val studentsByDirection = divisionStudentsByDirection(students)
        val studentsAfterSeparation = selectionByMinimalRating(studentsByDirection, withoutCheck)[Type.PASSED]!!
        val scholarshipByDirection = distributionScholarshipByDirection(studentsAfterSeparation)
        val studentsUniversitiesByDirection = divisionStudentsUniversitiesByDirection(studentsAfterSeparation)
        val scholarshipByUniversitiesIntoDirection =
            distributionScholarshipByUniversitiesIntoDirection(studentsUniversitiesByDirection, scholarshipByDirection)
        return getListWinningStudents(studentsUniversitiesByDirection, scholarshipByUniversitiesIntoDirection)
    }

    protected abstract fun divisionStudentsByDirection(students: List<StudentDTO>): Map<Long, List<StudentDTO>>

    protected abstract fun selectionByMinimalRating(
        students: Map<Long, List<StudentDTO>>,
        withoutCheck: Boolean
    ): Map<Type, Map<Long, List<StudentDTO>>>

    protected abstract fun distributionScholarshipByDirection(
        students: Map<Long, List<StudentDTO>>,
        quantity: Int = NUMBER_SCHOLARSHIPS
    ): Map<Long, Int>

    protected abstract fun divisionStudentsUniversitiesByDirection(students: Map<Long, List<StudentDTO>>): Map<Long, Map<Long, List<StudentDTO>>>

    protected abstract fun distributionScholarshipByUniversitiesIntoDirection(
        students: Map<Long, Map<Long, List<StudentDTO>>>,
        quantityMap: Map<Long, Int>
    ): Map<Long, Map<Long, Int>>

    protected abstract fun getListWinningStudents(
        students: Map<Long, Map<Long, List<StudentDTO>>>,
        scholarships: Map<Long, Map<Long, Int>>
    ): Map<Long, Map<Long, List<StudentDTO>>>
}