package com.fpinbo.kall

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class KallAdapterFactory private constructor() : CallAdapter.Factory() {

    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke() = KallAdapterFactory()
    }

    override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {
        val parameterizedReturnType = CallAdapter.Factory.getParameterUpperBound(0, returnType as ParameterizedType)
        return ResponseKallAdapter<Any>(parameterizedReturnType)
    }
}

private class ResponseKallAdapter<T>(
        private val responseType: Type
) : CallAdapter<T, Kall<T>> {

    override fun responseType() = responseType

    override fun adapt(call: retrofit2.Call<T>): Kall<T> = Kall.RetrofitKall(call)
}
