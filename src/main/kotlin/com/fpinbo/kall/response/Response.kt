package com.fpinbo.kall.response

import okhttp3.Headers

const val OK_STATUS_CODE = 200

sealed class Response<out A>(
        open val code: Int,
        open val headers: okhttp3.Headers,
        open val message: String?) {

    companion object {}

    data class Success<out A>(
            val body: A,
            override val code: Int,
            override val headers: Headers,
            override val message: String?
    ) : Response<A>(code, headers, message)

    data class Error<out Nothing>(
            val errorBody: okhttp3.ResponseBody,
            override val code: Int,
            override val headers: Headers,
            override val message: String?
    ) : Response<Nothing>(code, headers, message)
}
