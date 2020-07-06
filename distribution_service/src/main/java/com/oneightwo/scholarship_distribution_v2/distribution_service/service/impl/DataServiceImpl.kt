package com.oneightwo.scholarship_distribution_v2.distribution_service.service.impl

import com.oneightwo.scholarship_distribution_v2.constants.LOCALHOST
import com.oneightwo.scholarship_distribution_v2.constants.SCIENCE_DIRECTIONS_URL
import com.oneightwo.scholarship_distribution_v2.constants.STUDENTS_URL
import com.oneightwo.scholarship_distribution_v2.constants.UNIVERSITIES_URL
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.DataService
import com.oneightwo.scholarship_distribution_v2.models.ScienceDirectionDTO
import com.oneightwo.scholarship_distribution_v2.models.StudentDTO
import com.oneightwo.scholarship_distribution_v2.models.UniversityDTO
import com.oneightwo.scholarship_distribution_v2.tools.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class DataServiceImpl : DataService {

    private val log = logger(DataServiceImpl::class.java)

    @Autowired
    private lateinit var restTemplate: RestTemplate

    override fun getScienceDirections(): List<ScienceDirectionDTO> {
        val responseEntity = restTemplate.exchange("$LOCALHOST:7447$SCIENCE_DIRECTIONS_URL",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<ScienceDirectionDTO>>() {}
        )
        return responseEntity.body ?: listOf()
    }

    override fun getUniversities(): List<UniversityDTO> {
        val responseEntity = restTemplate.exchange("$LOCALHOST:7447$UNIVERSITIES_URL",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<UniversityDTO>>() {}
        )
        return responseEntity.body ?: listOf()
    }

    override fun getStudentByMonthAndYear(): List<StudentDTO> {
        val responseEntity = restTemplate.exchange("$LOCALHOST:7447$STUDENTS_URL",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<StudentDTO>>() {}
        )
        return responseEntity.body ?: listOf()
    }
}