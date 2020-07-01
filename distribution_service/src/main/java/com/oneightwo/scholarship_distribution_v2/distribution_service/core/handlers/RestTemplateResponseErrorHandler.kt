package com.oneightwo.scholarship_distribution_v2.distribution_service.core.handlers

import com.oneightwo.scholarship_distribution_v2.exceptions.ServiceException
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.ResponseErrorHandler

@Component
class RestTemplateResponseErrorHandler : ResponseErrorHandler {

    override fun hasError(response: ClientHttpResponse): Boolean {
        return (response.statusCode.series() == HttpStatus.Series.CLIENT_ERROR
                || response.statusCode.series() == HttpStatus.Series.SERVER_ERROR)
    }

    override fun handleError(response: ClientHttpResponse) {
        if (response.statusCode.series() == HttpStatus.Series.SERVER_ERROR) {
            throw ServiceException("")
        } else if (response.statusCode.series() == HttpStatus.Series.CLIENT_ERROR) {

        }
    }

}