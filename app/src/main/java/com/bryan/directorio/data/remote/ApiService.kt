package com.bryan.directorio.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/** Endpoints de https://jsonplaceholder.typicode.com usados por la app. */
interface ApiService {

    /** GET /users → lista de usuarios */
    @GET("users")
    suspend fun getUsers(): List<UserDto>

    /** GET /users/{id} → un usuario */
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserDto

    /** GET /posts?userId={id} → publicaciones del usuario */
    @GET("posts")
    suspend fun getPostsByUser(@Query("userId") userId: Int): List<PostDto>

    companion object {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

        fun create(): ApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
