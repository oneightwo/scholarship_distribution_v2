package com.oneightwo.scholarship_distribution_v2.storage_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer

@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
class StorageServiceApplication

fun main(args: Array<String>) {
    runApplication<StorageServiceApplication>(*args)

}
