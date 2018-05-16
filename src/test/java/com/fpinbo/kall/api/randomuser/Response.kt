package com.fpinbo.kall.api.randomuser

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Response(@JsonProperty("results") val results: List<Result>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Result(@JsonProperty("name") val name: Name)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Name(
    @JsonProperty("first") val first: String,
    @JsonProperty("last") val last: String
)