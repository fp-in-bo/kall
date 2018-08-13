package com.fpinbo.kall

import arrow.core.Either
import arrow.higherkind
import com.fpinbo.kall.response.Response

fun <A> Kall<A>.k(): KallK<A> = KallK(this)

fun <A> KallKOf<A>.value(): Kall<A> = this.fix().kall

@higherkind
class KallK<A>(val kall: Kall<A>) : KallKOf<A>, KallKKindedJ<A> {

    fun <B> map(f: (A) -> B): KallK<B> =
            kall.map(f).k()

    fun <B> ap(fa: KallKOf<(A) -> B>): KallK<B> =
            flatMap { a -> fa.fix().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> KallKOf<B>): KallK<B> =
            kall.flatMap { f(it).fix().kall }.k()

    companion object {

        fun <A> just(a: A): KallK<A> =
                Kall.just(a).k()


        tailrec fun <A, B> tailRecM(a: A, f: (A) -> KallKOf<Either<A, B>>): KallK<B> {
            val success = f(a).fix().value().execute() as Response.Success
            val either = success.body
            return when (either) {
                is Either.Left -> tailRecM(either.a, f)
                is Either.Right -> Kall.just(either.b).k()
            }
        }
    }
}