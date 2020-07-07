package com.oneightwo.scholarship_distribution_v2.distribution_service.service.impl

import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.MINIMUM_PERCENTAGE_BORDER
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.Type
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.DistributionService
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO
import org.springframework.stereotype.Service
import kotlin.math.round

@Service
class DistributionServiceImpl : DistributionService() {

    protected fun calculateMinimalRating(students: List<StudentDTO>): Double =
        students.sumBy { it.rating } / students.size * MINIMUM_PERCENTAGE_BORDER

    protected fun calculateAverageRating(students: List<StudentDTO>): Double =
        students.sumBy { it.rating }.toDouble() / students.size

    protected fun calculateAverageRatings(students: Map<Long, List<StudentDTO>>): Map<Long, Double> {
        val result = mutableMapOf<Long, Double>()
        students.forEach { (k, v) ->
            result[k] = calculateAverageRating(v)
        }
        return result
    }

    private fun distributionScholarshipByAny(
        students: Map<Long, List<StudentDTO>>,
        quantity: Int
    ): Map<Long, Int> {
        val result = mutableMapOf<Long, Int>()
        val iterationResult = mutableMapOf<Long, Int>()
        var averageRatingByAnything = calculateAverageRatings(students)
        val relationshipRatingByAnything = mutableMapOf<Long, Double>()
        var sumRating = averageRatingByAnything.values.sum()
        var free = quantity

        while (free != 0) {
            averageRatingByAnything.forEach { (k, v) ->
                relationshipRatingByAnything[k] = v / sumRating
            }
            if (relationshipRatingByAnything.values.all { round(it * free) == 0.0 }) {
                var idAny: Long?
                if (free > 0) {
                    idAny = averageRatingByAnything.maxBy { it.value }?.key
                    iterationResult[idAny!!] = 1
                } else {
                    idAny = averageRatingByAnything.minBy { it.value }?.key
                    iterationResult[idAny!!] = -1
                }
            } else {
                relationshipRatingByAnything.forEach { (k, v) ->
                    val preliminaryValue = round(v * free).toInt()
                    val freePlaces = students[k]!!.size - result.getOrDefault(k, 0)
                    if (preliminaryValue > freePlaces) {
                        iterationResult[k] = freePlaces
                    } else {
                        iterationResult[k] = preliminaryValue
                    }
                }
            }
            iterationResult.forEach { (k, v) ->
                result[k] = result.getOrDefault(k, 0) + v
            }
            free = quantity - result.values.sum()
            averageRatingByAnything = calculateAverageRatings(students.filter { (k, v) ->
                result.getOrDefault(k, -1) > v.size
            })
            sumRating = averageRatingByAnything.values.sum()
            iterationResult.clear()
            relationshipRatingByAnything.clear()
        }
        return result
    }

    protected fun sortByRating(students: Map<Long, Map<Long, List<StudentDTO>>>): Map<Long, Map<Long, List<StudentDTO>>> {
        val result = mutableMapOf<Long, MutableMap<Long, List<StudentDTO>>>()
        students.forEach { (kDir, sMap) ->
            if (kDir !in result.keys) {
                result[kDir] = mutableMapOf()
            }
            sMap.forEach { (kU, vList) ->
                if (kU !in result[kDir]!!.keys) {
                    result[kDir]!![kU] = vList.sortedBy { it.rating }
                }
            }
        }
        return result
    }

    override fun divisionStudentsByDirection(students: List<StudentDTO>): Map<Long, List<StudentDTO>> {
        val result = mutableMapOf<Long, MutableList<StudentDTO>>()
        students.forEach { item ->
            val key = item.scienceDirectionId!!
            if (key !in result.keys) {
                result[key] = arrayListOf()
            }
            result[key]?.add(item)
        }
        return result
    }

    override fun selectionByMinimalRating(
        students: Map<Long, List<StudentDTO>>,
        withoutCheck: Boolean
    ): Map<Type, Map<Long, List<StudentDTO>>> {
        val result = mutableMapOf<Type, MutableMap<Long, ArrayList<StudentDTO>>>().apply {
            put(Type.PASSED, mutableMapOf())
            put(Type.EXCLUDED, mutableMapOf())
        }
        students.forEach { (k, v) ->
            v.forEach { student ->
                val minRating = calculateMinimalRating(v)
                if ((withoutCheck || student.isValid) && student.rating >= minRating) {
                    if (k !in result[Type.PASSED]!!.keys) {
                        result[Type.PASSED]!![k] = arrayListOf()
                    }
                    result[Type.PASSED]!![k]!!.add(student)
                } else {
                    if (k !in result[Type.EXCLUDED]!!.keys) {
                        result[Type.EXCLUDED]!![k] = arrayListOf()
                    }
                    result[Type.EXCLUDED]!![k]!!.add(student)
                }
            }
        }
        return result
    }

    override fun distributionScholarshipByDirection(
        students: Map<Long, List<StudentDTO>>,
        quantity: Int
    ): Map<Long, Int> {
        return distributionScholarshipByAny(students, quantity)
    }

    /**
     * разделение студентов университетов по направлениям
     * @param students Map<Направление, Список студентов>
     * @return Map<Направление, Map<Университет, Список студентов>>
     */
    override fun divisionStudentsUniversitiesByDirection(students: Map<Long, List<StudentDTO>>): Map<Long, Map<Long, List<StudentDTO>>> {
        val result = mutableMapOf<Long, MutableMap<Long, ArrayList<StudentDTO>>>()
        students.forEach { (kDir, studentList) ->
            if (kDir !in result.keys) {
                result[kDir] = mutableMapOf()
            }
            studentList.forEach { student ->
                val kU = student.universityId!!
                if (kU !in result[kDir]!!.keys) {
                    result[kDir]!![kU] = arrayListOf()
                }
                result[kDir]!![kU]?.add(student)
            }
        }
        return result
    }

    override fun distributionScholarshipByUniversitiesIntoDirection(
        students: Map<Long, Map<Long, List<StudentDTO>>>,
        quantityMap: Map<Long, Int>
    ): Map<Long, Map<Long, Int>> {
        val result = mutableMapOf<Long, Map<Long, Int>>()
        students.forEach { (kDir, stMap) ->
            if (kDir !in result.keys) {
                result[kDir] = mutableMapOf()
            }
            result[kDir] = distributionScholarshipByAny(stMap, quantityMap.getOrDefault(kDir, 0))
        }
        return result
    }

    override fun getListWinningStudents(
        students: Map<Long, Map<Long, List<StudentDTO>>>,
        scholarships: Map<Long, Map<Long, Int>>
    ): Map<Long, Map<Long, List<StudentDTO>>> {
        val result = mutableMapOf<Long, MutableMap<Long, List<StudentDTO>>>()
        val sortStudents = sortByRating(students)
        sortStudents.forEach { (kDir, vMap) ->
            if (kDir !in result.keys) {
                result[kDir] = mutableMapOf()
            }
            vMap.forEach { (kU, vList) ->
                if (kU !in result[kDir]!!.keys) {
                    result[kDir]!![kU] = vList.slice(0..scholarships[kDir]!![kU]!!)
                }
            }
        }
        return result
    }
}