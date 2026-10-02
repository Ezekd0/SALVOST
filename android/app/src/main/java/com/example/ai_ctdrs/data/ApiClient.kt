package com.example.ai_ctdrs.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface AiCtdrsApiService {
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): Response<TokenResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserProfile>

    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserProfile>

    @GET("health")
    suspend fun healthCheck(): Response<HealthResponse>

    @POST("analysis/run")
    suspend fun runAnalysis(
        @Header("Authorization") token: String,
        @Body request: EventRequest,
    ): Response<SecurityEvent>

    @GET("dashboard/summary")
    suspend fun getDashboardSummary(@Header("Authorization") token: String): Response<DashboardSummary>

    @GET("incidents/")
    suspend fun getIncidents(@Header("Authorization") token: String): Response<List<Incident>>

    @GET("incidents/{id}")
    suspend fun getIncident(
        @Header("Authorization") token: String,
        @Path("id") incidentId: Int,
    ): Response<Incident>

    @GET("incidents/responses/recent")
    suspend fun getRecentResponses(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 10,
    ): Response<List<ResponseAction>>

    @GET("events/")
    suspend fun getEvents(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 25,
    ): Response<List<SecurityEvent>>
}

object ApiClient {
    fun create(baseUrl: String): AiCtdrsApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(45, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        return Retrofit.Builder()
            .baseUrl(normalizedBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AiCtdrsApiService::class.java)
    }
}
