package com.fpinbo.kall.response

fun <A, B> Response<A>.flatMap(f: (A) -> Response<B>): Response<B> =
        when (this) {
            is Response.Error -> Response.Error(errorBody, code, headers, message)
            is Response.Success -> f(body)
        }
