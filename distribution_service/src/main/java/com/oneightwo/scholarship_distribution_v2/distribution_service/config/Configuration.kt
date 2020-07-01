package com.oneightwo.scholarship_distribution_v2.distribution_service.config

import com.oneightwo.scholarship_distribution_v2.distribution_service.core.handlers.RestTemplateResponseErrorHandler
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class Configuration {

    @Bean
    fun getRestTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
//            .errorHandler(RestTemplateResponseErrorHandler())
            .build()
    }

}