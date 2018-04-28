package com.fpinbo.kall

fun <A, B> Kall<A>.map(f: (A) -> B): Kall<B> = Kall.Map(this, f)

fun <A, B> Kall<A>.flatMap(f: (A) -> Kall<B>): Kall<B> = Kall.FlatMap(this, f)