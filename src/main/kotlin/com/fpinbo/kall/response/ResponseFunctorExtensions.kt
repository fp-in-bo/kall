package com.fpinbo.kall.response

fun <A, T> Response<A>.fold(fe: (Response.Error<A>) -> T, fs: (Response.Success<A>) -> T): T {
    return when (this) {
        is Response.Error -> fe(this)
        is Response.Success -> fs(this)
    }
}

fun <A, B> Response<A>.map(f: (A) -> B): Response<B> {
    return flatMap({ Response.just(f(it)) })
}
