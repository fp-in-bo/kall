package com.fpinbo.kall

import com.fpinbo.kall.response.Response
import com.fpinbo.kall.response.flatMap
import com.fpinbo.kall.response.fold
import com.fpinbo.kall.response.map
import okhttp3.Request
import retrofit2.Callback


sealed class Kall<A> {

    abstract fun execute(): Response<A>

    abstract fun cancel()

    abstract fun clone(): Kall<A>

    abstract fun executeAsync(onResponse: (Kall<A>, Response<A>) -> Unit, onFailure: (Kall<A>, Throwable) -> Unit)

    abstract val cancelled: Boolean

    abstract val executed: Boolean

    abstract val request: okhttp3.Request

    data class RetrofitKall<A>(
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

        override val request: Request
            get() = retrofitCall.request()

        private fun buildResponse(response: retrofit2.Response<A>): Response<A> {
            return if (response.isSuccessful) {
                Response.Success(response.body()!!, response.code(), response.headers(), response.message())
            } else {
                Response.Error(response.errorBody()!!, response.code(), response.headers(), response.message())
            }
        }
    }

    data class Map<A, B>(
            private val original: Kall<A>,
            private val f: (A) -> B
    ) : Kall<B>() {
        override fun cancel() = original.cancel()

        override fun clone(): Kall<B> = Map(original, f)

        override fun executeAsync(onResponse: (Kall<B>, Response<B>) -> Unit, onFailure: (Kall<B>, Throwable) -> Unit) {
            original.executeAsync({ _, response ->
                onResponse(this, response.map(f))
            }, { _, t -> onFailure(this, t) })
        }

        override val cancelled: Boolean = original.cancelled
        override val executed: Boolean = original.executed
        override val request: Request = original.request

        override fun execute(): Response<B> = original.execute().map(f)
    }


    data class FlatMap<A, B>(
            private val original: Kall<A>,
            private val f: (A) -> Kall<B>
    ) : Kall<B>() {
        override fun cancel() = original.cancel()

        override fun clone(): Kall<B> = FlatMap(original, f)

        override fun executeAsync(onResponse: (Kall<B>, Response<B>) -> Unit,
                                  onFailure: (Kall<B>, Throwable) -> Unit) {

            original.executeAsync({ _, response ->
                val responseB = response.fold(
                        { Response.Error<B>(it.errorBody, it.code, it.headers, it.message) },
                        { f(it.body).execute() })
                onResponse(this, responseB)
            }, { _, t -> onFailure(this, t) })

        }

        override val cancelled: Boolean = original.cancelled
        override val executed: Boolean = original.executed
        override val request: Request = original.request

        override fun execute(): Response<B> = original.execute().flatMap { f(it).execute() }
    }
}