package com.fpinbo.kall.response

fun <A, B> Response<A>.flatMap(f: (A) -> Response<B>): Response<B> =
        fold({ Response.Error(it.errorBody, code, headers, message) }, { f(it.body) })

