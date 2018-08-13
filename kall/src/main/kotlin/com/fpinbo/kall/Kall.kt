package com.fpinbo.kall

import com.fpinbo.kall.response.Response
import com.fpinbo.kall.response.flatMap
import com.fpinbo.kall.response.fold
import com.fpinbo.kall.response.map
import retrofit2.Callback

sealed class Kall<A> {

    companion object {}

    abstract fun execute(): Response<A>

    abstract fun cancel()

    abstract fun clone(): Kall<A>

    abstract fun executeAsync(onResponse: (Kall<A>, Response<A>) -> Unit, onFailure: (Kall<A>, Throwable) -> Unit)

    abstract val cancelled: Boolean

    abstract val executed: Boolean

    internal data class RetrofitKall<A>(
            private val retrofitCall: retrofit2.Call<A>
    ) : Kall<A>() {
        override fun cancel() = retrofitCall.cancel()

        override fun clone() = RetrofitKall(retrofitCall.clone())

        override fun execute(): Response<A> {
            val response = retrofitCall.execute()
            return buildResponse(response)
        }

        override fun executeAsync(onResponse: (Kall<A>, Response<A>) -> Unit, onFailure: (Kall<A>, Throwable) -> Unit) {
            retrofitCall.enqueue(object : Callback<A> {

                override fun onResponse(call: retrofit2.Call<A>, response: retrofit2.Response<A>) {
                    onResponse(this@RetrofitKall, buildResponse(response))
                }

                override fun onFailure(call: retrofit2.Call<A>, t: Throwable) {
                    onFailure(this@RetrofitKall, t)
                }
            })
        }

        override val cancelled: Boolean
            get() = retrofitCall.isCanceled

        override val executed: Boolean
            get() = retrofitCall.isExecuted

        private fun buildResponse(response: retrofit2.Response<A>): Response<A> {
            return if (response.isSuccessful) {
                Response.Success(response.body()!!, response.code(), response.headers(), response.message())
            } else {
                Response.Error(response.errorBody()!!, response.code(), response.headers(), response.message())
            }
        }
    }

    internal data class Map<A, B>(
            private val original: Kall<A>,
            private val f: (A) -> B
    ) : Kall<B>() {
        override fun cancel() = original.cancel()

        override fun clone(): Kall<B> = Map(original.clone(), f)

        override fun executeAsync(onResponse: (Kall<B>, Response<B>) -> Unit, onFailure: (Kall<B>, Throwable) -> Unit) {
            original.executeAsync({ _, response ->
                onResponse(this, response.map(f))
            }, { _, t -> onFailure(this, t) })
        }

        override val cancelled: Boolean = original.cancelled
        override val executed: Boolean = original.executed

        override fun execute(): Response<B> = original.execute().map(f)
    }

    internal data class FlatMap<A, B>(
            private val original: Kall<A>,
            private val f: (A) -> Kall<B>
    ) : Kall<B>() {
        override fun cancel() = original.cancel()

        override fun clone(): Kall<B> = FlatMap(original.clone(), f)

        override fun executeAsync(onResponse: (Kall<B>, Response<B>) -> Unit,
                                  onFailure: (Kall<B>, Throwable) -> Unit) {

            original.executeAsync({ _, response ->
                response.fold(
                        { Response.Error<B>(it.errorBody, it.code, it.headers, it.message) },
                        {
                            f(it.body).executeAsync({ _, res ->
                                res.fold(
                                        { Response.Error<B>(it.errorBody, it.code, it.headers, it.message) },
                                        { onResponse(this, res) })
                            }, { _, t -> onFailure(this, t) })
                        })

            }, { _, t -> onFailure(this, t) })
        }

        override val cancelled: Boolean = original.cancelled
        override val executed: Boolean = original.executed

        override fun execute(): Response<B> = original.execute().flatMap { f(it).execute() }
    }

    internal data class JustKall<A>(private val value: Response<A>) : Kall<A>() {

        private var mutableCancelled = false
        private var mutableExecuted = false

        override fun cancel() {
            mutableCancelled = true
        }

        override fun clone(): Kall<A> = this

        override fun execute(): Response<A> {
            mutableExecuted = true
            return value
        }

        override fun executeAsync(onResponse: (Kall<A>, Response<A>) -> Unit, onFailure: (Kall<A>, Throwable) -> Unit) {
            mutableExecuted = true
            onResponse(this, value)
        }

        override val cancelled: Boolean
            get() = mutableCancelled

        override val executed: Boolean
            get() = mutableExecuted
    }
}
