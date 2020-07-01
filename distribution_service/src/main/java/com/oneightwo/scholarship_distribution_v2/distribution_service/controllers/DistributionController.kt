package com.oneightwo.scholarship_distribution_v2.distribution_service.controllers

import com.oneightwo.scholarship_distribution_v2.constants.DISTRIBUTION_URL
import com.oneightwo.scholarship_distribution_v2.constants.STUDENTS_URL
import com.oneightwo.scholarship_distribution_v2.constants.UNIVERSITIES_URL
import com.oneightwo.scholarship_distribution_v2.distribution_service.service.DataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@RestController
@RequestMapping(value = [DISTRIBUTION_URL])
class DistributionController {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var dataService: DataService

    @GetMapping
    fun getAll(): ResponseEntity<Any> {
        dataService.getScienceDirections()
        return ResponseEntity.ok(restTemplate.getForObject("http://localhost:7447$UNIVERSITIES_URL"))
    }

}