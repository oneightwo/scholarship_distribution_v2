package com.oneightwo.scholarship_distribution_v2.distribution_service.service.impl

import com.oneightwo.scholarship_distribution_v2.constants.Semester
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.MINIMUM_PERCENTAGE_BORDER
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.NUMBER_SCHOLARSHIPS
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.Type
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.DataService
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.DistributionService
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO
import com.oneightwo.scholarship_distribution_v2.tools.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.math.round

@Component
class DistributionServiceImpl : DistributionService {

    private val log = logger(DistributionServiceImpl::class.java)

    @Autowired
    private lateinit var dataService: DataService

    /**
     * расчет среднего рейтинга из списка студентов (по направлению)
     * @param students список студентов (одного навпрления)
     * @return рейтинг
     */
    private fun getAverageRating(students: List<StudentDTO>): Double {
        var rating = 0.0
        for (student in students) {
            rating += student.rating
        }
        return rating / students.size
    }

    /**
     * граница минимального рейтинга (по навпрлению)
     * @param students список студентов (одного направления)
     * @return рейтинг
     */
    private fun getMinimalRating(students: List<StudentDTO>): Double {
        var rating = 0.0
        for (student in students) {
            rating += student.rating
        }
        return rating / students.size * MINIMUM_PERCENTAGE_BORDER
    }

    /**
     * распределение студентов по направлениям
     * @param students список студентов
     * @return Map<Направление>, Список студентов>
     */
    private fun divisionOfDirection(students: List<StudentDTO>): Map<Long, MutableList<StudentDTO>> {
        val divisionList = mutableMapOf<Long, MutableList<StudentDTO>>().apply {
            dataService.getScienceDirections().forEach {
                if (it.id != null) {
                    put(it.id!!, arrayListOf())
                }
            }
        }
        for (student in students) {
            divisionList[student.scienceDirectionId]!!.add(student)
        }
        return divisionList
    }

    /**
     * разделение студентов на: рейтинг >= минимальному (PASSED) или рейтинг < минимального(NOT_PASSED)
     * @param directionsStudentsMap Map<Направление>, Список студентов>
     * @return Map<Тип (прошли, не прошли), Map<Направление, Список студентов>>
     */
    private fun selectionByMinimalRatingAndDirection(directionsStudentsMap: Map<Long, List<StudentDTO>>): Map<Type, Map<Long, List<StudentDTO>>> {
        val selectionMap = mutableMapOf<Type, Map<Long, ArrayList<StudentDTO>>>().apply {
            put(Type.PASSED, mutableMapOf<Long, ArrayList<StudentDTO>>().apply {
                directionsStudentsMap.forEach { (k, _) ->
                    put(k, arrayListOf())
                }
            })
            put(Type.EXCLUDED, mutableMapOf<Long, ArrayList<StudentDTO>>().apply {
                directionsStudentsMap.forEach { (k, _) ->
                    put(k, arrayListOf())
                }
            })
        }

        directionsStudentsMap.forEach { (_, v) ->
            v.forEach { student ->
                val minRating = getMinimalRating(v)
                val keySD = student.scienceDirectionId!!
                selectionMap[if (student.rating < minRating || !student.isValid) Type.EXCLUDED else Type.PASSED]!![keySD]!!.add(
                    student
                )
            }
        }
        return selectionMap
    }

    /**
     * перерасчет среднего рейтинга направлениию или университету по прошедшим студентам
     * @param directionsStudentsMap Map<Направление>, Список студентов> прошедших студентов
     * @return Map<Направление>, Средний рейтинг>
     */
    private fun calculationAverageRatingByAnything(directionsStudentsMap: Map<Long, List<StudentDTO>>): Map<Long, Double> {
        val ratingMap = mutableMapOf<Long, Double>()
        directionsStudentsMap.forEach { (k, v) ->
            var counter = 0.0
            v.forEach { student ->
                counter += student.rating
            }
            ratingMap[k] = counter / v.size
        }
        return ratingMap
    }

    /**
     * универсальный метод распределения количества стипендий по направлениям/университетам
     * @param anyStudentsMap Map<Any (направления/университеты), Список студентов>
     * @param quantity количество стипендий для распреления
     * @return Map<Any (направления/университеты), Количество стипендий>
     */
    private fun distributionScholarshipByAny(
        anyStudentsMap: Map<Long, List<StudentDTO>>,
        quantity: Long
    ): Map<Long, Long> {
        val result = mutableMapOf<Long, Long>()
        val iterationResult = mutableMapOf<Long, Long>()
        var averageRatingByAnything = calculationAverageRatingByAnything(anyStudentsMap)
        val relationshipRatingByAnything = mutableMapOf<Long, Double>()
        var sumRating = averageRatingByAnything.values.sum()
        var free = quantity

        while (free != 0L) {
            averageRatingByAnything.forEach { (k, v) ->
                relationshipRatingByAnything[k] = v / sumRating
            }
            if (relationshipRatingByAnything.values.all { round(it * free) == 0.0 }) {
                var idAny: Long?
                if (free > 0) {
                    idAny = averageRatingByAnything.maxBy { it.value }?.key
                    iterationResult[idAny!!] = 1L
                } else {
                    idAny = averageRatingByAnything.minBy { it.value }?.key
                    iterationResult[idAny!!] = -1L
                }
            } else {
                relationshipRatingByAnything.forEach { (k, v) ->
                    val preliminaryValue = (v * free).toLong()
                    val freePlaces = anyStudentsMap[k]!!.size.toLong() - result.getOrDefault(k, 0)
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
            averageRatingByAnything = calculationAverageRatingByAnything(anyStudentsMap.filter { (k, v) ->
                result.getOrDefault(k, -1) > v.size
            })
            sumRating = averageRatingByAnything.values.sum()
            iterationResult.clear()
            relationshipRatingByAnything.clear()
        }
        return result
    }

    /**
     * разделение студентов университетов по направлениям
     * @param directionsStudentsMap Map<Направление, Список студентов>
     * @return Map<Направление, Map<Университет, Список студентов>>
     */
    private fun divisionUniversitiesByDirection(directionsStudentsMap: Map<Long, List<StudentDTO>>): MutableMap<Long, MutableMap<Long, ArrayList<StudentDTO>>> {
        val result = mutableMapOf<Long, MutableMap<Long, ArrayList<StudentDTO>>>().apply {
            dataService.getScienceDirections().forEach { sdit ->
                put(sdit.id!!, mutableMapOf<Long, ArrayList<StudentDTO>>().apply {
                    dataService.getUniversities().forEach { uit ->
                        put(uit.id!!, arrayListOf())
                    }
                })
            }
        }

        directionsStudentsMap.forEach { (_, v) ->
            v.forEach {
                result[it.scienceDirectionId]!![it.universityId]!!.add(it)
            }
        }
        return result
    }

    /**
     * распределение стипендий по университетам внутри направлений
     * @param directionsMapUniversitiesStudentsMap Map<Направление, Map<Университет, Список студентов>>
     * @param directionsQuantityMap Map<Направление, Количество стипендий>
     * @return Map<Направление, Map<Университет, Количество стипендий>>
     */
    private fun distributionOfScholarshipsByUniversityWithinDirections(
        directionsMapUniversitiesStudentsMap: Map<Long, Map<Long, List<StudentDTO>>>,
        directionsQuantityMap: Map<Long, Long>
    ): MutableMap<Long, Map<Long, Long>> {
        val result = mutableMapOf<Long, Map<Long, Long>>()

        directionsQuantityMap.forEach { (k, v) ->
            if (directionsMapUniversitiesStudentsMap.containsKey(k)) {
                result[k] = distributionScholarshipByAny(directionsMapUniversitiesStudentsMap[k]!!, v)
            }
        }
        return result
    }

    /**
     * сортировка студентов внутри направления в университетах по рейтингу
     * @param directionsMapUniversitiesStudentsMap Map<Направление, Map<Университет, Список студентов>>
     * @return Map<Направление, Map<Университет, Список студентов>>
     */
    private fun sortedByRatingsWithinUniversities(
        directionsMapUniversitiesStudentsMap: Map<Long, Map<Long, List<StudentDTO>>>
    ): MutableMap<Long, Map<Long, List<StudentDTO>>> {

        directionsMapUniversitiesStudentsMap.forEach { (_, v) ->
            v.values.map { lit ->
                lit.sortedBy { sit -> sit.rating }
            }
        }
        return directionsMapUniversitiesStudentsMap.toMutableMap()
    }

    /**
     * получение студетнов победителей по университетам
     * @param quantityPlaces Map<Направление, Map<Университет, Количество стипендий>>
     * @param directionsMapUniversitiesStudentsMap Map<Направление, Map<Университет, Список студентов>>
     * @return Map<Университет, Список студентов>
     */
    private fun getWinnersStudents(
        quantityPlaces: Map<Long, Map<Long, Long>>,
        directionsMapUniversitiesStudentsMap: Map<Long, Map<Long, List<StudentDTO>>>
    ): Map<Long, List<StudentDTO>> {
        val winnersList = mutableMapOf<Long, ArrayList<StudentDTO>>()

        quantityPlaces.forEach { (d, v) ->
            v.forEach { (u, v1) ->
                if (!winnersList.containsKey(u)) winnersList[u] = arrayListOf()
                winnersList[u]!!.addAll(directionsMapUniversitiesStudentsMap[d]!![u]!!.slice(0..v1.toInt()))
            }
        }
        return winnersList
    }

    private fun distributionScholarship(students: List<StudentDTO>):  {

    }

    override fun getCountScholarshipsByDirectionAndUniversities(
        semester: Semester,
        year: Int
    ): Map<Long, Map<Long, Long>> {
        return mapOf()
    }

    override fun getWinnerStudents(semester: Semester, year: Int): Map<Long, List<StudentDTO>> {
        val students = dataService.getStudentByMonthAndYear()
        val divisionOfDirection = divisionOfDirection(students)
        val selectionByMinimalRatingAndDirection = selectionByMinimalRatingAndDirection(divisionOfDirection)
        val distributionScholarshipByAny =
            distributionScholarshipByAny(selectionByMinimalRatingAndDirection[Type.PASSED]!!, NUMBER_SCHOLARSHIPS)
        val divisionUniversitiesByDirection =
            divisionUniversitiesByDirection(selectionByMinimalRatingAndDirection[Type.PASSED]!!)
        val distributionOfScholarshipsByUniversityWithinDirections =
            distributionOfScholarshipsByUniversityWithinDirections(
                divisionUniversitiesByDirection,
                distributionScholarshipByAny
            )
        return getWinnersStudents(distributionOfScholarshipsByUniversityWithinDirections, sortedByRatingsWithinUniversities(divisionUniversitiesByDirection))
    }

    override fun getLoserStudents(semester: Semester, year: Int): Map<String, List<StudentDTO>> {
        TODO("Not yet implemented")
    }

    override fun getReportByDirection(semester: Semester, year: Int): List<Map<String, String>> {
        TODO("Not yet implemented")
    }

    override fun getReportByDirectionAndUniversities(
        semester: Semester,
        year: Int
    ): Map<String, List<Map<String, String>>> {
        TODO("Not yet implemented")
    }
}