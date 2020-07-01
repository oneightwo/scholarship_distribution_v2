package com.oneightwo.scholarship_distribution_v2.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import java.io.Serializable

data class ResponseBody (
    val status: Int,
    val name: String,
    val message: String?,
    @JsonProperty("detail_message")
    val detailMessage: String?
) : Serializable