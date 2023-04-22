package com.taskruler

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Object calls Retrofit to
 * @return list of tasks from public JSON data hosted at https://wrytheruc.github.io/
 */
object RetrofitClientInstance {
    private var retrofit: Retrofit? = null
    private const val BASE_URL = "https://wrytheruc.github.io/"

    val retrofitInstance : Retrofit?
        get() {
            // has this object been created yet?
            if (retrofit == null) {
                // create it
                retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}