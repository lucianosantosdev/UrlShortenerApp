package dev.lssoftware.urlshortenerapp.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.POST
import dev.lssoftware.urlshortenerapp.BuildConfig
import retrofit2.http.Body

interface UrlShortenerAPI {
    @POST("api/alias")
    suspend fun shortenUrl(
        @Body aliasRequest: AliasRequestDto
    ): Response<AliasResponseDto>

    companion object {
        fun create(): UrlShortenerAPI {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply{
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                })
                .build()
            return Retrofit.Builder()
                .baseUrl("https://url-shortener-server.onrender.com/")
                .client(client)
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(UrlShortenerAPI::class.java)

        }
    }
}