package com.fpinbo.kall

import com.fpinbo.kall.response.Response
import com.fpinbo.kall.response.just

fun <A, B> Kall<A>.ap(ff: Kall<(A) -> B>): Kall<B> = ff.flatMap { f -> this.map(f) }

fun <A> Kall.Companion.just(value: A): Kall<A> = Kall.JustKall(Response.just(value))

fun <A> Kall.Companion.error(response: Response.Error<A>): Kall<A> = Kall.JustKall(response)

operator fun <T> Kall.Companion.invoke(value: T) = just(value)