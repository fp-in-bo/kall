package com.fpinbo.kall

import com.fpinbo.kall.response.Response

interface Kall<A> {

    companion object {}

    fun cancel()

    fun clone(): Kall<A>

    fun execute(): Response<A>

    fun executeAsync(onResponse: (Kall<A>, Response<A>) -> Unit, onFailure: (Kall<A>, Throwable) -> Unit)

    val cancelled: Boolean

    val executed: Boolean
}