package com.oneightwo.scholarship_distribution_v2.distribution_service.service.impl

import com.oneightwo.scholarship_distribution_v2.constants.LOCALHOST
import com.oneightwo.scholarship_distribution_v2.constants.SCIENCE_DIRECTIONS_URL
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.DataService
import com.oneightwo.scholarship_distribution_v2.models.ScienceDirectionDTO
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
}