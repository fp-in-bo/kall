package com.fpinbo.kall

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.effects.IO
import retrofit2.Callback

sealed class KallMe<A> {

    companion object {}

    abstract fun cancel()

    abstract fun clone(): KallMe<A>

    //    abstract fun run(): EitherT<ForIO, Throwable, A>
    // TODO find a proper type for computational errors (or leave this to the user, putting an E type param)
    // TODO find a way to achieve a signature like: fun run(): IO<Either<Throwable, A>>
    abstract fun run(): IO<Either<Throwable, retrofit2.Response<A>>>

    abstract val cancelled: Boolean

    abstract val executed: Boolean

    internal data class RetrofitKall<A>(
            private val retrofitCall: retrofit2.Call<A>
    ) : KallMe<A>() {
        override fun cancel() = retrofitCall.cancel()

        override fun clone() = RetrofitKall(retrofitCall.clone())

        override fun run(): IO<Either<Throwable, retrofit2.Response<A>>> {

            return IO.async<Either<Throwable, retrofit2.Response<A>>> { callback ->
                retrofitCall.enqueue(object : Callback<A> {

                    override fun onResponse(call: retrofit2.Call<A>, response: retrofit2.Response<A>) {
                        // that's weird, but it's due to the double error channel:
                        // 1- IO errors
                        // 2- errors due to either
                        callback(Either.right(response.right()))
                    }

                    override fun onFailure(call: retrofit2.Call<A>, t: Throwable) {
                        // TODO pair the error with the call
                        callback(t.left())
                    }
                })
            }

        }

        override val cancelled: Boolean
            get() = retrofitCall.isCanceled

        override val executed: Boolean
            get() = retrofitCall.isExecuted

    }

}
