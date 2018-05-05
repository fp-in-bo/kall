package com.fpinbo.kall

import com.fpinbo.kall.response.Response
import retrofit2.Callback

class KallImpl<A>(
    private val retrofitCall: retrofit2.Call<A>
) : Kall<A> {

    override fun cancel() = retrofitCall.cancel()

    override fun clone() = KallImpl(retrofitCall.clone())

    override fun execute(): Response<A> {
        val response = retrofitCall.execute()
        return buildResponse(response)
    }

    override fun executeAsync(onResponse: (Kall<A>, Response<A>) -> Unit, onFailure: (Kall<A>, Throwable) -> Unit) {
        retrofitCall.enqueue(object : Callback<A> {

            override fun onResponse(call: retrofit2.Call<A>, response: retrofit2.Response<A>) {
                onResponse(this@KallImpl, buildResponse(response))
            }

            override fun onFailure(call: retrofit2.Call<A>, t: Throwable) {
                onFailure(this@KallImpl, t)
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