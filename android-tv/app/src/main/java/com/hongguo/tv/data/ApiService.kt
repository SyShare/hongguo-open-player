package com.hongguo.tv.data

import com.hongguo.tv.CatalogResponse
import com.hongguo.tv.PlayInfo
import com.hongguo.tv.SearchResponse
import com.hongguo.tv.SeriesDetail
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/api/catalog")
    suspend fun getCatalog(
        @Query("page") page: Int = 1,
        @Query("category") category: String? = null,
        @Query("genre") genre: String? = null
    ): CatalogResponse

    @GET("/api/search")
    suspend fun search(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 1
    ): SearchResponse

    @GET("/api/series/{seriesId}")
    suspend fun getSeries(@Path("seriesId") seriesId: String): SeriesDetail

    @GET("/api/play/{vid}")
    suspend fun getPlay(@Path("vid") vid: String, @Query("quality") quality: String = "auto"): PlayInfo
}

object ApiConfig {
    private const val DEFAULT_BASE_URL = "http://127.0.0.1:8899"
    private const val ENV_BASE_URL = "HONGGUO_TV_API_BASE_URL"

    fun baseUrl(): String {
        return System.getenv(ENV_BASE_URL)?.takeIf { it.isNotBlank() } ?: DEFAULT_BASE_URL
    }
}

object ApiFactory {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun create(): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.baseUrl())
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
