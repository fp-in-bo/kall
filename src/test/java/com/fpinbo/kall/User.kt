package com.fpinbo.kall

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
        @JsonProperty("login") val login: String,
        @JsonProperty("followers_url") val followersUrl: String
)