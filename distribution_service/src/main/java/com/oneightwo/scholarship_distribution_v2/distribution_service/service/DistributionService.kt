package com.oneightwo.scholarship_distribution_v2.distribution_service.service

import com.oneightwo.scholarship_distribution_v2.constants.Semester
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.KEF
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.UR
import com.oneightwo.scholarship_distribution_v2.distribution_service.constants.VK
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO
import kotlin.math.roundToInt


interface DistributionService {
    /**
     * расчет рейтинга по критериям
     * @param criteria массив критериев длиной 15, диапазоном 0-5
     * @return рейтинг
     */
    fun calculationRating(criteria: IntArray): Int {
        var rating = 0.0
        for (i in criteria.indices) {
            rating += KEF[VK[i]] * UR[criteria[i]]
        }
        return rating.roundToInt()
    }

    /**
     * распределение стипендий внутри направлений по университемам
     * @param semester семестр распределения
     * @param year год распределения
     * @return Map<Направление></Направление>, Map<Университет></Университет>, Кол-во стипендий>>
     */
    fun getCountScholarshipsByDirectionAndUniversities(semester: Semester, year: Int): Map<Long, Map<Long, Long>>

    /**
     * получение студентов победителей
     * @param semester семестр
     * @param year год
     * @return Map<Универсиет></Универсиет>, Студенты>
     */
    fun getWinnerStudents(semester: Semester, year: Int): Map<Long, List<StudentDTO>>

    /**
     * получение не прошедших отбор студентов
     * @param semester семестр
     * @param year год
     * @return Map<Универсиет></Универсиет>, Студенты>
     */
    fun getLoserStudents(semester: Semester, year: Int): Map<Long, List<StudentDTO>>

    /**
     * формирование отчета по направлениям
     * @param semester семестр
     * @param year год
     * @return List<Map></Map><Направление></Направление>, Значение>>
     */
    fun getReportByDirection(semester: Semester, year: Int): List<Map<String, String>>

    /**
     * формирование отчета внутри напрвления по университетам
     * @param semester семестер
     * @param year год
     * @return Map<Направление></Направление>, List<Map></Map><Университет></Университет>, Значение>>>
     */
    fun getReportByDirectionAndUniversities(semester: Semester, year: Int): Map<String, List<Map<String, String>>>
}