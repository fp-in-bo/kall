package com.fpinbo.kall.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Stargazer(
    @JsonProperty("avatar_url") val avatarUrl: String,
    @JsonProperty("login") val userName: String
)