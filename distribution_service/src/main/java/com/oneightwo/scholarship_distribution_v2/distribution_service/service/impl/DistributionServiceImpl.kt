package com.oneightwo.scholarship_distribution_v2.distribution_service.service.impl

import com.oneightwo.scholarship_distribution_v2.constants.Semester
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.MINIMUM_PERCENTAGE_BORDER
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.Type
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.DataService
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.DistributionService
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@Component
class DistributionServiceImpl : DistributionService {

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
        val divisionList = hashMapOf<Long, MutableList<StudentDTO>>().apply {
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
    private fun selectionByMinimalRatingAndDirection(directionsStudentsMap: Map<Long, List<StudentDTO>>): Map<Type, Map<Long, ArrayList<StudentDTO>>> {
        val selectionMap = hashMapOf<Type, Map<Long, ArrayList<StudentDTO>>>().apply {
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
     * перерасчет среднего рейтинга направлениий по прошедшим студентам
     * @param directionsStudentsMap Map<Направление>, Список студентов> прошедших студентов
     * @return Map<Направление>, Средний рейтинг>
     */
    private fun calculationRatingForDirections(directionsStudentsMap: Map<Long, ArrayList<StudentDTO>>): Map<Long, Double> {
        val ratingMap: MutableMap<Long, Double> = hashMapOf()

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
        anyStudentsMap: Map<String, List<StudentDTO>>?,
        quantity: Long
    ): Map<String, Long?> {
        var scholarshipByCandidates: MutableMap<String, Long?> =
            object : HashMap<String?, Long?>() {
                init {
                    anyStudentsMap!!.forEach { (key: String?, value: List<StudentDTO>?) -> put(key, -1L) }
                }
            }
        val result: MutableMap<String, Long?> = object : HashMap<String?, Long?>() {
            init {
                anyStudentsMap!!.forEach { (key: String?, value: List<StudentDTO>?) ->
                    put(
                        key,
                        0L
                    )
                }
            }
        }
        val relationshipRatingByCandidates: MutableMap<String, Double> =
            HashMap()
        var ratingByDirection =
            calculationRatingForDirections(anyStudentsMap)
        var sumRating: Double = sumDouble(ratingByDirection.values)
        var free = quantity
        while (free != 0L) {
            val finalSumRating = sumRating
            ratingByDirection.forEach { (key: String, value: Double?) ->
                relationshipRatingByCandidates[key] = value!! / finalSumRating
            }
            val finalFree = free
            val finalFree1 = free
            if (relationshipRatingByCandidates.values.stream()
                    .allMatch { v: Double -> Math.round(v * finalFree1) == 0L }
            ) {
                var finalKey = ""
                if (free > 0) {
                    log.info("result => {}", result)
                    scholarshipByCandidates = result.entries.stream()
                        .filter { e: Map.Entry<String, Long?> ->
                            anyStudentsMap!![e.key]!!.size > result[e.key]!!
                        }
                        .collect(
                            Collectors.toMap<Map.Entry<String, Long?>, String, Long?>(
                                { java.util.Map.Entry.key }
                            ) { java.util.Map.Entry.value }
                        )
                    log.info("scholarshipByCandidates => {}", scholarshipByCandidates)
                    var maxRating = 0.0
                    for ((key, value) in ratingByDirection) {
                        if (maxRating < value!! && scholarshipByCandidates.containsKey(key)) {
                            maxRating = value
                            finalKey = key
                        }
                    }
                    scholarshipByCandidates[finalKey] = result[finalKey]!! + 1L
                } else {
                    var minRating: Double? = 101.0
                    scholarshipByCandidates = result.entries.stream()
                        .filter { e: Map.Entry<String, Long?> -> e.value!! >= 1 }
                        .collect(
                            Collectors.toMap<Map.Entry<String, Long?>, String, Long?>(
                                { java.util.Map.Entry.key }
                            ) { java.util.Map.Entry.value }
                        )
                    val finalScholarshipByCandidates: Map<String, Long?> =
                        scholarshipByCandidates
                    ratingByDirection = calculationRatingForDirections(
                        anyStudentsMap!!.entries.stream()
                            .filter { e: Map.Entry<String, List<StudentDTO>> ->
                                finalScholarshipByCandidates.containsKey(e.key)
                            }
                            .collect(
                                Collectors.toMap<Map.Entry<String, List<StudentDTO>>, String, List<StudentDTO>>(
                                    { java.util.Map.Entry.key }
                                ) { java.util.Map.Entry.value }
                            )
                    )
                    log.info("scholarshipByCandidates = {}", scholarshipByCandidates)
                    log.info("ratingByDirection = {}", ratingByDirection)
                    for (key in scholarshipByCandidates.keys) {
                        if (ratingByDirection.containsKey(key) && minRating!! > ratingByDirection[key]!!) {
                            minRating = ratingByDirection[key]
                            finalKey = key
                        }
                    }
                    log.info("minRating = {}", minRating)
                    log.info("keyMinRating = {}", finalKey)
                    scholarshipByCandidates[finalKey] = scholarshipByCandidates[finalKey]!! - 1L
                }
                result[finalKey] = scholarshipByCandidates[finalKey]
            } else {
                for ((s, v) in relationshipRatingByCandidates) {
                    if (scholarshipByCandidates.containsKey(s)) {
                        scholarshipByCandidates[s] = Math.round(finalFree * relationshipRatingByCandidates[s]!!)
                    }
                }
                for (k in scholarshipByCandidates.keys) {
                    while (scholarshipByCandidates[k]!! > anyStudentsMap!![k]!!.size - result[k]!!) {
                        scholarshipByCandidates[k] = scholarshipByCandidates[k]!! - 1
                    }
                }
                for ((key, value) in scholarshipByCandidates) {
                    if (result.containsKey(key)) {
                        result[key] = result[key]!! + value!!
                    } else {
                        result[key] = value
                    }
                }
            }
            scholarshipByCandidates = scholarshipByCandidates.entries.stream()
                .filter { e: Map.Entry<String, Long?> ->
                    result[e.key] != anyStudentsMap!![e.key]!!.size
                }
                .collect(
                    Collectors.toMap<Map.Entry<String, Long?>, String, Long?>(
                        { java.util.Map.Entry.key }
                    ) { java.util.Map.Entry.value }
                )
            val finalScholarshipByCandidates1: Map<String, Long?> = scholarshipByCandidates
            ratingByDirection = calculationRatingForDirections(
                anyStudentsMap!!.entries.stream()
                    .filter { e: Map.Entry<String, List<StudentDTO>> ->
                        finalScholarshipByCandidates1.containsKey(e.key)
                    }
                    .collect(
                        Collectors.toMap<Map.Entry<String, List<StudentDTO>>, String, List<StudentDTO>>(
                            { java.util.Map.Entry.key }
                        ) { java.util.Map.Entry.value }
                    )
            )
            sumRating = sumDouble(ratingByDirection.values)
            relationshipRatingByCandidates.clear()
            free = quantity - sumLong(result.values)
            log.info("result -> {}", result)
            log.info("resultSum -> {}", sumLong(result.values))
            log.info("count -> {}", quantity)
            log.info("free -> {}", free)
        }
        return result
    }

    /**
     * разделение студентов университетов по направлениям
     * @param directionsStudentsMap Map<Направление></Направление>, Список студентов>
     * @return Map<Направление></Направление>, Map<Университет></Университет>, Список студентов>>
     */
    private fun divisionUniversitiesByDirection(directionsStudentsMap: Map<String, List<StudentDTO>>): Map<String, MutableMap<String, List<StudentDTO>?>?>? {
        val map: MutableMap<String, MutableMap<String, List<StudentDTO>?>?> =
            object :
                HashMap<String?, Map<String?, List<StudentDTO?>?>?>() {
                init {
                    scienceDirectionService.getAll().forEach { scienceDirection ->
                        put(
                            scienceDirection.getName(),
                            HashMap<String, List<StudentDTO>>()
                        )
                    }
                }
            }
        for (d in map.keys) {
            for (student in directionsStudentsMap[d]!!) {
                val u: String = student.getUniversity().getAbbreviation()
                val listMap: MutableMap<String, List<StudentDTO>?>? =
                    map[d]
                if (listMap!!.containsKey(u)) {
                    listMap[u].add(student)
                } else {
                    listMap[u] = ArrayList<StudentDTO>()
                    listMap[u].add(student)
                }
                map[d] = listMap
            }
        }
        for (k in map.keys) {
            var c = 0
            for (k1 in map[k]!!.keys) {
                c += map[k]!![k1]!!.size
            }
            log.info("k={}, size({})", k, c)
        }
        return map
    }

    /**
     * распределение стипендий по университетам внутри направлений
     * @param directionsMapUniversitiesStudentsMap Map<Направление></Направление>, Map<Университет></Университет>, Список студентов>>
     * @param directionsQuantityMap Map<Направление></Направление>, Количество стипендий>
     * @return Map<Направление></Направление>, Map<Университет></Университет>, Количество стипендий>>
     */
    private fun distributionOfScholarshipsByUniversityWithinDirections(
        directionsMapUniversitiesStudentsMap: Map<String, Map<String, List<StudentDTO>>>,
        directionsQuantityMap: Map<String, Long>
    ): Map<String, Map<String, Long>>? {
        val distributionScholarshipByUniversities: MutableMap<String, Map<String, Long>> =
            HashMap()
        for ((k, v) in directionsQuantityMap) {
            distributionScholarshipByUniversities[k] =
                distributionScholarshipByAny(directionsMapUniversitiesStudentsMap[k], v)
        }
        for ((key, value) in distributionScholarshipByUniversities) {
            var sum = 0
            for ((_, value1) in value) {
                (sum += value1).toInt()
            }
            log.info("На {} выделено {}", key, sum)
        }
        return distributionScholarshipByUniversities
    }

    /**
     * сортировка студентов внутри направления в университетах по рейтингу
     * @param directionsMapUniversitiesStudentsMap Map<Направление></Направление>, Map<Университет></Университет>, Список студентов>>
     * @return Map<Направление></Направление>, Map<Университет></Университет>, Список студентов>>
     */
    private fun sortedByRatingsWithinUniversities(directionsMapUniversitiesStudentsMap: Map<String, Map<String, List<StudentDTO>>>): Map<String, Map<String, List<StudentDTO>>>? {
        val sortedByDirectionsAndUniversities: MutableMap<String, Map<String, List<StudentDTO>>> =
            HashMap<String, Map<String, List<StudentDTO>>>()
        for ((key, value) in directionsMapUniversitiesStudentsMap) {
            var list: List<StudentDTO> = ArrayList<StudentDTO>()
            val map: MutableMap<String, List<StudentDTO>> =
                HashMap<String, List<StudentDTO>>()
            sortedByDirectionsAndUniversities[key] = value
            for ((key1, value1) in value) {
                list = value1.stream()
                    .sorted(Comparator.comparingInt<Any>(StudentDTO::getRating).reversed())
                    .collect(Collectors.toList<Any>())
                map[key1] = list
            }
            sortedByDirectionsAndUniversities[key] = map
        }
        return sortedByDirectionsAndUniversities
    }

    /**
     * получение студетнов победителей по университетам
     * @param quantityPlaces Map<Направление></Направление>, Map<Университет></Университет>, Количество стипендий>>
     * @param directionsMapUniversitiesStudentsMap Map<Направление></Направление>, Map<Университет></Университет>, Список студентов>>
     * @return Map<Университет></Университет>, Список студентов>
     */
    private fun getWinnersStudents(
        quantityPlaces: Map<String, Map<String, Long>>,
        directionsMapUniversitiesStudentsMap: Map<String, Map<String, List<StudentDTO>>>
    ): Map<String, List<StudentDTO>?>? {
        val winnersList: MutableMap<String, List<StudentDTO>?> =
            HashMap<String, List<StudentDTO>?>()
        for ((key, value) in directionsMapUniversitiesStudentsMap) {
            for ((key1, value1) in value) {
                if (winnersList.containsKey(key1)) {
                    val list: List<StudentDTO>? = winnersList[key1]
                    list.addAll(value1.subList(0, quantityPlaces[key]!![key1]!!.toInt()))
                    winnersList[key1] = list
                } else {
                    winnersList[key1] = value1.subList(0, quantityPlaces[key]!![key1]!!.toInt())
                }
                log.info(
                    "направление=({}) вуз=({}) мест=({}) поместится=({})",
                    key,
                    key1,
                    quantityPlaces[key]!![key1],
                    winnersList[key1]!!.size + value1.subList(
                        0,
                        quantityPlaces[key]!![key1]!!.toInt()
                    ).size
                )
            }
        }
        var sum = 0
        for ((key, value) in winnersList) {
            log.info("k={}, v={}", key, value!!.size)
            sum += value.size
        }
        log.info("sum={}", sum)
        return winnersList
    }

    override fun getCountScholarshipsByDirectionAndUniversities(
        semester: Semester,
        year: Int
    ): Map<String, Map<String, Long>> {
        TODO("Not yet implemented")
    }

    override fun getWinnerStudents(semester: Semester, year: Int): Map<String, List<StudentDTO>> {
        TODO("Not yet implemented")
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