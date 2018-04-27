package com.fpinbo.kall.kallsealedclass

import com.fpinbo.kall.response.Response
import com.fpinbo.kall.response.flatMap
import com.fpinbo.kall.response.map
import okhttp3.Request


sealed class Kall<A> {

    abstract fun execute(): Response<A>

    abstract fun cancel()

    abstract fun clone(): Kall<A>

    abstract fun executeAsync(onResponse: (Kall<A>, Response<A>) -> Unit, onFailure: (Kall<A>, Throwable) -> Unit)

    abstract val cancelled: Boolean

    abstract val executed: Boolean

    abstract val request: okhttp3.Request


    data class Map<A, B>(
            private val original: Kall<A>,
            private val f: (A) -> B
    ) : Kall<B>() {
        override fun cancel() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun clone(): Kall<B> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun executeAsync(onResponse: (Kall<B>, Response<B>) -> Unit, onFailure: (Kall<B>, Throwable) -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override val cancelled: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val executed: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val request: Request
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

        override fun execute(): Response<B> = original.execute().map(f)
    }


    data class FlatMap<A, B>(
            private val original: Kall<A>,
            private val f: (A) -> Kall<B>
    ) : Kall<B>() {
        override fun cancel() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun clone(): Kall<B> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun executeAsync(onResponse: (Kall<B>, Response<B>) -> Unit, onFailure: (Kall<B>, Throwable) -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override val cancelled: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val executed: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val request: Request
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

        override fun execute(): Response<B> = original.execute().flatMap { f(it).execute() }

    }


}