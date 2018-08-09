package com.fpinbo.kall

import arrow.Kind
import arrow.instance
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

@instance(KallK::class)
interface KallKFunctorInstance : Functor<ForKallK> {

    override fun <A, B> Kind<ForKallK, A>.map(f: (A) -> B): KallK<B> =
            fix().map(f)
}

@instance(KallK::class)
interface KallKApplicativeInstance : Applicative<ForKallK> {
    override fun <A, B> KallKOf<A>.ap(ff: KallKOf<(A) -> B>): KallK<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForKallK, A>.map(f: (A) -> B): KallK<B> =
            fix().map(f)

    override fun <A> just(a: A): KallK<A> =
            KallK.just(a)
}

@instance(KallK::class)
interface KallKMonadInstance : Monad<ForKallK> {

    override fun <A, B> KallKOf<A>.map(f: (A) -> B): KallK<B> =
            fix().map(f)

    override fun <A, B> KallKOf<A>.ap(ff: KallKOf<(A) -> B>): KallK<B> =
            fix().ap(ff)

    override fun <A, B> KallKOf<A>.flatMap(f: (A) -> Kind<ForKallK, B>): KallK<B> =
            fix().flatMap(f)

    override fun <A> just(a: A): KallK<A> =
            KallK.just(a)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, KallKOf<arrow.core.Either<A, B>>>): KallK<B> =
            KallK.tailRecM(a, f)
}