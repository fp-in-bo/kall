package com.fpinbo.kall

fun <A, B> Kall<A>.map(f: (A) -> B): Kall<B> = MapKall(this, f)
