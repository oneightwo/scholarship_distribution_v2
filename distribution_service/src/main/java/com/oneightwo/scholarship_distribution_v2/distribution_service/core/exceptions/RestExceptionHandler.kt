package com.oneightwo.scholarship_distribution_v2.distribution_service.core.exceptions

import com.oneightwo.scholarship_distribution_v2.models.ResponseBody
import com.oneightwo.scholarship_distribution_v2.tools.logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Exception
import java.net.ConnectException

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = logger(RestExceptionHandler::class.java)

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val responseBody = ResponseBody(
            status.value(),
            status.name,
            ex.message,
            ex.localizedMessage
        )
        return ResponseEntity(responseBody, status)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val responseBody = ResponseBody(
            status.value(),
            status.name,
            ex.message,
            ex.localizedMessage
        )
        return ResponseEntity(responseBody, status)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val responseBody = ResponseBody(
            status.value(),
            status.name,
            ex.message,
            ex.localizedMessage
        )
        return ResponseEntity(responseBody, status)
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val responseBody = ResponseBody(
            status.value(),
            status.name,
            ex.message,
            ex.localizedMessage
        )
        return ResponseEntity(responseBody, status)
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError::class)
    protected fun handleInternalServerError(ex: HttpServerErrorException.InternalServerError): ResponseEntity<Any> {
        val responseBody = ResponseBody(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.name,
            ex.message,
            ex.localizedMessage
        )
        return ResponseEntity(responseBody, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(ConnectException::class)
    protected fun handleConnectError(ex: ConnectException): ResponseEntity<Any> {
        val responseBody = ResponseBody(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.name,
            ex.message,
            ex.localizedMessage
        )
        return ResponseEntity(responseBody, HttpStatus.INTERNAL_SERVER_ERROR)
    }


}