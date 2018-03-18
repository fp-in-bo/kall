package com.fpinbo.kall

fun <A, B> Kall<A>.map(f: (A) -> B): Kall<B> = MapKall(this, f)

fun <A, B> Kall<A>.flatMap(f: (A) -> Kall<B>): Kall<B> = FlatMapKall(this, f)