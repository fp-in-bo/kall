package com.fpinbo.kall.response

import okhttp3.Headers

private val emptyHeaders = Headers.of(emptyMap())

fun <A, B> Response<A>.ap(ff: Response<((A) -> B)>): Response<B> {
    return fold(
        { Response.Error(it.errorBody, code, headers, message) },
        { thisSuccess ->
            ff.fold(
                { Response.Error(it.errorBody, it.code, it.headers, it.message) },
                { Response.Success(it.body.invoke(thisSuccess.body), code, headers, message) }
            )
        }
    )
}

fun <A> Response.Companion.just(value: A): Response<A> {
    return Response.Success(
        value,
        OK_STATUS_CODE,
        emptyHeaders,
        null
    )
}

operator fun <T> Response.Companion.invoke(value: T): Response<T> = just(value)