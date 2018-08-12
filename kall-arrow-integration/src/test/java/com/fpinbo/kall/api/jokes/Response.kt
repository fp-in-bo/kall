package com.fpinbo.kall.api.jokes

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Response(@JsonProperty("value") val value: Value)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Value(@JsonProperty("joke") val joke: String)
