package com.virajjage.abl_test_viraj_jage.network

import com.virajjage.abl_test_viraj_jage.constants.AppAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private lateinit var retrofit: Retrofit
    fun getRetrofit(): Retrofit {

        try {

            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build()

            retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(AppAPI.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return retrofit
    }

}