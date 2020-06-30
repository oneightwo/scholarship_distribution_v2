package com.oneightwo.scholarship_distribution_v2.distribution_service.service.impl

import com.oneightwo.scholarship_distribution_v2.constants.Semester
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.DistributionService
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.iterator
import kotlin.collections.set

@Component
class DistributionServiceImpl : DistributionService {

    @Autowired
    private val studentService: StudentServiceImpl? = null

    @Autowired
    private val scienceDirectionService: ScienceDirectionServiceImpl? = null

    private val log = LoggerFactory.getLogger(DistributionServiceImpl::class.java)


    /**
     * расчет среднего рейтинга из списка студентов (по направлению)
     * @param students список студентов (одного навпрления)
     * @return рейтинг
     */
    private fun getAverageRating(students: List<StudentDTO>?): Double {
        var rating = 0.0
        for (student in students!!) {
            rating += student.getRating()
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
            rating += student.getRating()
        }
        return rating / students.size * MINIMUM_PERCENTAGE_BORDER
    }

    /**
     * распределение студентов по направлениям
     * @param students список студентов
     * @return Map<Направление></Направление>, Список студентов>
     */
    private fun divisionOfDirection(students: List<StudentDTO>): Map<String, MutableList<StudentDTO>> {
        val divisionList: Map<String, MutableList<StudentDTO>> =
            object : HashMap<String?, List<StudentDTO?>?>() {
                init {
                    scienceDirectionService.getAll()
                        .forEach { scienceDirection -> put(scienceDirection.getName(), ArrayList<StudentDTO>()) }
                }
            }
        log.info(divisionList.toString())
        for (student in students) {
            val nameDirection: String = student.getScienceDirection().getName()
            divisionList[nameDirection]!!.add(student)
        }
        return divisionList
    }

    /**
     * разделение студентов на: рейтинг >= минимальному (PASSED) или рейтинг < минимального(NOT_PASSED)
     * @param directionsStudentsMap Map<Направление></Направление>, Список студентов>
     * @return Map<Тип (прошли, не прошли), Map></Тип><Направление></Направление>, Список студентов>>
     */
    private fun selectionByMinimalRatingAndDirection(directionsStudentsMap: Map<String, MutableList<StudentDTO>>): Map<String, Map<String?, List<StudentDTO?>?>> {
        val selectionList: Map<String, Map<String?, List<StudentDTO?>?>> =
            object :
                HashMap<String?, Map<String?, List<StudentDTO?>?>?>() {
                init {
                    put(PASSED, object : HashMap<String?, List<StudentDTO?>?>() {
                        init {
                            for (key in directionsStudentsMap.keys) {
                                put(key, ArrayList<StudentDTO>())
                            }
                        }
                    })
                    put(NOT_PASSED, object : HashMap<String?, List<StudentDTO?>?>() {
                        init {
                            for (key in directionsStudentsMap.keys) {
                                put(key, ArrayList<StudentDTO>())
                            }
                        }
                    })
                }
            }
        for ((key, value) in directionsStudentsMap) {
            log.info(key + " -> " + getMinimalRating(value).toString())
            for (student in value) {
                val minRating = getMinimalRating(value)
                val keySD: String = student.getScienceDirection().getName()
                if (student.getRating() < minRating || !student.isValid()) {
                    selectionList[NOT_PASSED]!![keySD].add(student)
                } else {
                    selectionList[PASSED]!![keySD].add(student)
                }
            }
        }
        return selectionList
    }

    /**
     * перерасчет среднего рейтинга направлениий по прошедшим студентам
     * @param directionsStudentsMap Map<Направление></Направление>, Список студентов> прошедших студентов
     * @return Map<Направление></Направление>, Средний рейтинг>
     */
    private fun calculationRatingForDirections(directionsStudentsMap: Map<String, List<StudentDTO>>?): Map<String, Double?> {
        val ratingMap: MutableMap<String, Double?> =
            HashMap()
        for ((key, studentsValues) in directionsStudentsMap!!) {
            var counter = 0.0
            for (student in studentsValues) {
                counter += student.getRating()
            }
            ratingMap[key] = counter / studentsValues.size
        }
        return ratingMap
    }

    /**
     * универсальный метод распределения количества стипендий по направлениям/университетам
     * @param anyStudentsMap Map<Any (направления></Any>/университеты), Список студентов>
     * @param quantity количество стипендий для распреления
     * @return Map<Any (направления></Any>/университеты), Количество стипендий>
     */
    private fun distributionScholarshipByAny(
        anyStudentsMap: Map<String, List<StudentDTO>>?,
        quantity: Long?
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
                    .allMatch { v: Double -> Math.round(v * finalFree1!!) == 0L }
            ) {
                var finalKey = ""
                if (free!! > 0) {
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
                        scholarshipByCandidates[s] = Math.round(finalFree!! * relationshipRatingByCandidates[s]!!)
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
    private fun divisionUniversitiesByDirection(directionsStudentsMap: Map<String, List<StudentDTO>>?): Map<String, MutableMap<String, List<StudentDTO>?>?> {
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
            for (student in directionsStudentsMap!![d]!!) {
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
        directionsMapUniversitiesStudentsMap: Map<String, MutableMap<String, List<StudentDTO>?>?>,
        directionsQuantityMap: Map<String, Long?>
    ): Map<String, Map<String, Long>> {
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
    private fun sortedByRatingsWithinUniversities(directionsMapUniversitiesStudentsMap: Map<String, MutableMap<String, List<StudentDTO>?>?>): Map<String, Map<String, List<StudentDTO>?>?> {
        val sortedByDirectionsAndUniversities: MutableMap<String, Map<String, List<StudentDTO>?>?> =
            HashMap<String, Map<String, List<StudentDTO>?>?>()
        for ((key, value) in directionsMapUniversitiesStudentsMap) {
            var list: List<StudentDTO> = ArrayList<StudentDTO>()
            val map: MutableMap<String, List<StudentDTO>?> =
                HashMap<String, List<StudentDTO>?>()
            sortedByDirectionsAndUniversities[key] = value
            for ((key1, value1) in value!!) {
                list = value1!!.stream()
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
        directionsMapUniversitiesStudentsMap: Map<String, Map<String, List<StudentDTO>?>?>
    ): Map<String, List<StudentDTO>?>? {
        val winnersList: MutableMap<String, List<StudentDTO>?> =
            HashMap<String, List<StudentDTO>?>()
        for ((key, value) in directionsMapUniversitiesStudentsMap) {
            for ((key1, value1) in value!!) {
                if (winnersList.containsKey(key1)) {
                    val list: List<StudentDTO>? = winnersList[key1]
                    list.addAll(value1!!.subList(0, quantityPlaces[key]!![key1]!!.toInt()))
                    winnersList[key1] = list
                } else {
                    winnersList[key1] =
                        value1!!.subList(0, quantityPlaces[key]!![key1]!!.toInt())
                }
                log.info(
                    "направление=({}) вуз=({}) мест=({}) поместится=({})",
                    key,
                    key1,
                    quantityPlaces[key]!![key1],
                    winnersList[key1]!!.size + value1!!.subList(
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

    fun getWinnerStudents(
        semester: Semester?,
        year: Int
    ): Map<String, List<StudentDTO>?>? {
        val studentList: List<StudentDTO> = studentService.getStudentBySemesterAndYear(semester, year)
        val divisionOfDirectionMap: Map<String, MutableList<StudentDTO>> =
            divisionOfDirection(studentList)
        val selectionByMinimalRatingAndDirectionMap: Map<String, Map<String?, List<StudentDTO?>?>> =
            selectionByMinimalRatingAndDirection(divisionOfDirectionMap)
        //        Map<String, Double> calculationRatingByDirectionMap = calculationRating(selectionByMinimalRatingAndDirectionMap.get(PASSED));
        val distributionScholarshipByDirection =
            distributionScholarshipByAny(selectionByMinimalRatingAndDirectionMap[PASSED], NUMBER_SCHOLARSHIPS)
        log.info("distributionScholarshipByDirection---->{}", distributionScholarshipByDirection)
        val divisionUniversitiesByDirection: Map<String, MutableMap<String, List<StudentDTO>?>?> =
            divisionUniversitiesByDirection(selectionByMinimalRatingAndDirectionMap[PASSED])
        val distributionScholarshipByUniversities =
            distributionOfScholarshipsByUniversityWithinDirections(
                divisionUniversitiesByDirection,
                distributionScholarshipByDirection
            )
        return getWinnersStudents(
            distributionScholarshipByUniversities,
            sortedByRatingsWithinUniversities(divisionUniversitiesByDirection)
        )
    }

    fun getLoserStudents(
        semester: Semester?,
        year: Int
    ): Map<String?, List<StudentDTO?>?>? {
        val studentList: List<StudentDTO> = studentService.getStudentBySemesterAndYear(semester, year)
        val divisionOfDirectionMap: Map<String, MutableList<StudentDTO>> =
            divisionOfDirection(studentList)
        val selectionByMinimalRatingAndDirectionMap: Map<String, Map<String?, List<StudentDTO?>?>> =
            selectionByMinimalRatingAndDirection(divisionOfDirectionMap)
        return selectionByMinimalRatingAndDirectionMap[NOT_PASSED]
    }

    fun getCountScholarshipsByDirectionAndUniversities(
        semester: Semester?,
        year: Int
    ): Map<String, Map<String, Long>> {
        val studentList: List<StudentDTO> = studentService.getStudentBySemesterAndYear(semester, year)
        val divisionOfDirectionMap: Map<String, MutableList<StudentDTO>> =
            divisionOfDirection(studentList)
        val selectionByMinimalRatingAndDirectionMap: Map<String, Map<String?, List<StudentDTO?>?>> =
            selectionByMinimalRatingAndDirection(divisionOfDirectionMap)
        //        Map<String, Double> calculationRatingByDirectionMap = calculationRating(selectionByMinimalRatingAndDirectionMap.get(PASSED));
        val distributionScholarshipByDirection =
            distributionScholarshipByAny(selectionByMinimalRatingAndDirectionMap[PASSED], NUMBER_SCHOLARSHIPS)
        log.info("distributionScholarshipByDirection---->{}", distributionScholarshipByDirection)
        val divisionUniversitiesByDirection: Map<String, MutableMap<String, List<StudentDTO>?>?> =
            divisionUniversitiesByDirection(selectionByMinimalRatingAndDirectionMap[PASSED])
        return distributionOfScholarshipsByUniversityWithinDirections(
            divisionUniversitiesByDirection,
            distributionScholarshipByDirection
        )
    }

    fun getReportByDirection(
        semester: Semester?,
        year: Int
    ): List<Map<String, String>>? {
        val reportList: MutableList<Map<String, String>> =
            ArrayList()
        val studentList: List<StudentDTO> = studentService.getStudentBySemesterAndYear(semester, year)
        val divisionOfDirectionMap: Map<String, MutableList<StudentDTO>> =
            divisionOfDirection(studentList)
        val selectionByMinimalRatingAndDirectionMap: Map<String, Map<String?, List<StudentDTO?>?>> =
            selectionByMinimalRatingAndDirection(divisionOfDirectionMap)
        val distributionScholarshipByDirection =
            distributionScholarshipByAny(selectionByMinimalRatingAndDirectionMap[PASSED], NUMBER_SCHOLARSHIPS)
        val map: MutableMap<String?, String> = HashMap()

        //Кол-во заявок по направлениям
        for ((key, value) in divisionOfDirectionMap) {
            map[key] = value.size.toString()
        }
        reportList.add(object : HashMap<String?, String?>() {
            init {
                putAll(map)
            }
        })
        //Средний рейтинг
        for ((key, value) in divisionOfDirectionMap) {
            map[key] = getAverageRating(value).toString()
        }
        reportList.add(object : HashMap<String?, String?>() {
            init {
                putAll(map)
            }
        })
        //Граница 25%
        for ((key, value) in divisionOfDirectionMap) {
            map[key] = getMinimalRating(value).toString()
        }
        reportList.add(object : HashMap<String?, String?>() {
            init {
                putAll(map)
            }
        })
        //Кол-во исключенных заявок
        for ((key, value) in selectionByMinimalRatingAndDirectionMap) {
            if (key == NOT_PASSED) {
                for ((key1, value1) in value) {
                    map[key1] = if (value1 != null) value1.size else 0.toString()
                }
            }
        }
        reportList.add(object : HashMap<String?, String?>() {
            init {
                putAll(map)
            }
        })
        //Кол-во прошедших заявок
        for ((key, value) in selectionByMinimalRatingAndDirectionMap) {
            if (key == PASSED) {
                for ((key1, value1) in value) {
                    map[key1] = if (value1 != null) value1.size else 0.toString()
                }
            }
        }
        reportList.add(object : HashMap<String?, String?>() {
            init {
                putAll(map)
            }
        })
        //Рекомендованное кол-во стипендий
        for ((key, value) in distributionScholarshipByDirection) {
            map[key] = value.toString()
        }
        reportList.add(object : HashMap<String?, String?>() {
            init {
                putAll(map)
            }
        })
        return reportList
    }

    fun getReportByDirectionAndUniversities(
        semester: Semester?,
        year: Int
    ): Map<String, List<Map<String, String>>>? {
        val reportList: MutableMap<String, List<Map<String, String>>> =
            HashMap()
        val studentList: List<StudentDTO> = studentService.getStudentBySemesterAndYear(semester, year)
        val divisionOfDirectionMap: Map<String, MutableList<StudentDTO>> =
            divisionOfDirection(studentList)
        val selectionByMinimalRatingAndDirectionMap: Map<String, Map<String?, List<StudentDTO?>?>> =
            selectionByMinimalRatingAndDirection(divisionOfDirectionMap)
        //        Map<String, Double> calculationRatingByDirectionMap = calculationRating(selectionByMinimalRatingAndDirectionMap.get(PASSED));
        val distributionScholarshipByDirection =
            distributionScholarshipByAny(selectionByMinimalRatingAndDirectionMap[PASSED], NUMBER_SCHOLARSHIPS)
        log.info("distributionScholarshipByDirection---->{}", distributionScholarshipByDirection)
        val divisionUniversitiesByDirectionMap: Map<String, MutableMap<String, List<StudentDTO>?>?> =
            divisionUniversitiesByDirection(selectionByMinimalRatingAndDirectionMap[PASSED])
        val distributionScholarshipByUniversities =
            distributionOfScholarshipsByUniversityWithinDirections(
                divisionUniversitiesByDirectionMap,
                distributionScholarshipByDirection
            )
        val winnerList: Map<String, List<StudentDTO>?>? = getWinnersStudents(
            distributionScholarshipByUniversities,
            sortedByRatingsWithinUniversities(divisionUniversitiesByDirectionMap)
        )
        //        return distributionScholarshipsByStudent(winnerList);
        val map: MutableMap<String, String> = HashMap()
        val list: MutableList<Map<String, String>> =
            ArrayList()

        //Средний рейтинг
        for ((key, value) in divisionUniversitiesByDirectionMap) {
            for ((key1, value1) in value!!) {
                map[key1] = getAverageRating(value1).toString()
            }
            list.add(object : HashMap<String?, String?>() {
                init {
                    putAll(map)
                }
            })
            reportList[key] = object : ArrayList<Map<String?, String?>?>() {
                init {
                    addAll(list)
                }
            }
            list.clear()
            map.clear()
        }
        //Кол-во заявок допущенных до конкурса
        for ((key, value) in sortedByRatingsWithinUniversities(
            divisionUniversitiesByDirectionMap
        )) {
            for ((key1, value1) in value!!) {
                map[key1] = value1!!.size.toString()
            }
            list.addAll(reportList[key]!!)
            list.add(object : HashMap<String?, String?>() {
                init {
                    putAll(map)
                }
            })
            reportList[key] = object : ArrayList<Map<String?, String?>?>() {
                init {
                    addAll(list)
                }
            }
            list.clear()
            map.clear()
        }
        //Расчетное кол-во стипендий
        for ((key, value) in getCountScholarshipsByDirectionAndUniversities(
            semester,
            year
        )) {
            for ((key1, value1) in value) {
                map[key1] = value1.toString()
            }
            list.addAll(reportList[key]!!)
            list.add(object : HashMap<String?, String?>() {
                init {
                    putAll(map)
                }
            })
            reportList[key] = object : ArrayList<Map<String?, String?>?>() {
                init {
                    addAll(list)
                }
            }
            list.clear()
            map.clear()
        }
        return reportList
    }
}