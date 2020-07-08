package com.oneightwo.scholarship_distribution_v2.distribution_service.controllers

import com.oneightwo.scholarship_distribution_v2.constants.DISTRIBUTION_URL
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.DistributionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [DISTRIBUTION_URL])
class DistributionController {

    @Autowired
    @Qualifier("distributionServiceImpl")
    private lateinit var distributionService: DistributionService

    @GetMapping
    fun getAll(): ResponseEntity<Any> {
        distributionService.execute()
        distributionService1
    }

}