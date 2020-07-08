package com.oneightwo.scholarship_distribution_v2.distribution_service.service.impl

import com.oneightwo.scholarship_distribution_v2.constants.Semester
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.Type
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.ReportService
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList

@Service
class ReportServiceImpl : ReportService, DistributionServiceImpl() {

    private fun divisionStudentByUniversities(students: List<StudentDTO>): Map<String, List<StudentDTO>> {
        return mutableMapOf<String, ArrayList<StudentDTO>>().apply {
            students.forEach { student ->
                val key = student.universityId!!.toString()
                if (key !in keys) {
                    put(key, arrayListOf())
                }
                this[key]!!.add(student)
            }
        }
    }

    private fun divisionStudentByUniversities(students: Map<Long, List<StudentDTO>>): Map<String, List<StudentDTO>> {
        return mutableMapOf<String, ArrayList<StudentDTO>>().apply {
            students.forEach { (_, v) ->
                v.forEach {
                    val key = dataService.getUniversity(it.universityId!!).name
                    if (key !in keys) {
                        put(key, arrayListOf())
                    }
                    this[key]!!.add(it)
                }
            }
        }
    }

    override fun getAllNumberApplicationsByUniversities(semester: Semester, year: Int): Map<String, Int> {
        val result = sortedMapOf<String, Int>()
        val students = dataService.getStudentByMonthAndYear(semester, year)
        divisionStudentByUniversities(students).forEach { (k, v) ->
            if (k !in result.keys) {
                result[k] = v.size
            }
        }
        return result
    }

    override fun getPassedNumberApplicationsByUniversities(semester: Semester, year: Int): Map<String, Int> {
        val result = sortedMapOf<String, Int>()
        val students = dataService.getStudentByMonthAndYear(semester, year)
        val studentsByDirection = divisionStudentsByDirection(students)
        val studentsAfterSeparation = selectionByMinimalRating(studentsByDirection, false)[Type.PASSED]!!
        divisionStudentByUniversities(studentsAfterSeparation).forEach { (k, v) ->
            if (k !in result.keys) {
                result[k] = v.size
            }
        }
        return result
    }

    override fun getReportByDirection(semester: Semester, year: Int): List<Map<String, String>> {
        val result = arrayListOf<SortedMap<String, String>>()
        val students = dataService.getStudentByMonthAndYear(semester, year)
        val studentsByDirection = divisionStudentsByDirection(students)
        //количество заявок
        result.add(sortedMapOf<String, String>().apply {
            studentsByDirection.forEach { (k, v) ->
                put(dataService.getScienceDirection(k).name, v.size.toString())
            }
        })
        //средний рейтинг
        result.add(sortedMapOf<String, String>().apply {
            studentsByDirection.forEach { (k, v) ->
                put(dataService.getScienceDirection(k).name, calculateAverageRating(v).toString())
            }
        })
        //граница 25%
        result.add(sortedMapOf<String, String>().apply {
            studentsByDirection.forEach { (k, v) ->
                put(dataService.getScienceDirection(k).name, calculateMinimalRating(v).toString())
            }
        })
        val studentsAfterSeparation = selectionByMinimalRating(studentsByDirection, false)
        //количество исключенных заявок
        result.add(sortedMapOf<String, String>().apply {
            studentsAfterSeparation[Type.EXCLUDED]!!.forEach { (k, v) ->
                put(dataService.getScienceDirection(k).name, v.size.toString())
            }
        })
        //количество допущенных заявок
        result.add(sortedMapOf<String, String>().apply {
            studentsAfterSeparation[Type.PASSED]!!.forEach { (k, v) ->
                put(dataService.getScienceDirection(k).name, v.size.toString())
            }
        })
        val scholarshipByDirection = distributionScholarshipByDirection(studentsAfterSeparation[Type.EXCLUDED]!!)
        //рекомендованное количество стипендий
        result.add(sortedMapOf<String, String>().apply {
            scholarshipByDirection.forEach { (k, v) ->
                put(dataService.getScienceDirection(k).name, v.toString())
            }
        })
        return result
    }

    override fun getReportByUniversitiesIntoDirections(
        semester: Semester,
        year: Int
    ): Map<String, List<Map<String, String>>> {
        val students = dataService.getStudentByMonthAndYear(semester, year)
        val studentsByDirection = divisionStudentsByDirection(students)
        val studentsAfterSeparation = selectionByMinimalRating(studentsByDirection, false)[Type.PASSED]!!
        val scholarshipByDirection = distributionScholarshipByDirection(studentsAfterSeparation)
        val studentsUniversitiesByDirection = divisionStudentsUniversitiesByDirection(studentsAfterSeparation)
        val scholarshipByUniversitiesIntoDirection =
            distributionScholarshipByUniversitiesIntoDirection(studentsUniversitiesByDirection, scholarshipByDirection)

    }

    override fun getLoserStudents(semester: Semester, year: Int): Map<String, List<StudentDTO>> {
        TODO("Not yet implemented")
    }
}