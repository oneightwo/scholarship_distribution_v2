package com.oneightwo.scholarship_distribution_v2.authorization_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer

@SpringBootApplication
@EnableAuthorizationServer
class AuthorizationServiceApplication

fun main(args: Array<String>) {
	runApplication<AuthorizationServiceApplication>(*args)
}
