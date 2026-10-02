package com.example.ai_ctdrs.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ai_ctdrs_auth")

class AiCtdrsRepository(private val context: Context) {
    private val baseUrlKey = stringPreferencesKey("backend_base_url")
    private val tokenKey = stringPreferencesKey("auth_token")

    suspend fun getSavedBaseUrl(): String {
        val preferences = context.dataStore.data.first()
        return preferences[baseUrlKey] ?: DEFAULT_BASE_URL
    }

    suspend fun setSavedBaseUrl(value: String) {
        val sanitized = value.trim().removeSuffix("/")
        context.dataStore.edit { it[baseUrlKey] = sanitized }
    }

    suspend fun getToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[tokenKey]
    }

    suspend fun setToken(token: String) {
        context.dataStore.edit { it[tokenKey] = token }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(tokenKey) }
    }

    suspend fun login(username: String, password: String): UserProfile {
        val service = ApiClient.create(getSavedBaseUrl())
        val response = service.login(username, password)
        if (!response.isSuccessful || response.body() == null) {
            throw HttpException(response)
        }
        val token = "Bearer ${response.body()!!.accessToken}"
        setToken(token)
        return getCurrentUser(token)
    }

    suspend fun register(request: RegisterRequest): UserProfile {
        val service = ApiClient.create(getSavedBaseUrl())
        val response = service.register(request)
        if (!response.isSuccessful || response.body() == null) {
            throw HttpException(response)
        }
        return response.body()!!
    }

    suspend fun getCurrentUser(token: String): UserProfile {
        val service = ApiClient.create(getSavedBaseUrl())
        val response = service.getCurrentUser(token)
        if (!response.isSuccessful || response.body() == null) {
            throw HttpException(response)
        }
        return response.body()!!
    }

    suspend fun healthCheck(): HealthResponse {
        val service = ApiClient.create(getSavedBaseUrl())
        val response = service.healthCheck()
        if (!response.isSuccessful || response.body() == null) {
            throw HttpException(response)
        }
        return response.body()!!
    }

    suspend fun getDashboardSummary(token: String): DashboardSummary {
        val service = ApiClient.create(getSavedBaseUrl())
        val response = service.getDashboardSummary(token)
        if (!response.isSuccessful || response.body() == null) {
            throw HttpException(response)
        }
        return response.body()!!
    }

    suspend fun getIncidents(token: String): List<Incident> {
        val service = ApiClient.create(getSavedBaseUrl())
        val response = service.getIncidents(token)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        return response.body() ?: emptyList()
    }

    suspend fun getRecentResponses(token: String, limit: Int = 10): List<ResponseAction> {
        val service = ApiClient.create(getSavedBaseUrl())
        val response = service.getRecentResponses(token, limit)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        return response.body() ?: emptyList()
    }

    suspend fun getEvents(token: String, limit: Int = 25): List<SecurityEvent> {
        val service = ApiClient.create(getSavedBaseUrl())
        val response = service.getEvents(token, limit)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        return response.body() ?: emptyList()
    }

    suspend fun runAnalysis(token: String, request: EventRequest): SecurityEvent {
        val service = ApiClient.create(getSavedBaseUrl())
        val response = service.runAnalysis(token, request)
        if (!response.isSuccessful || response.body() == null) {
            throw HttpException(response)
        }
        return response.body()!!
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://ai-ctdrs-backend.onrender.com/"
    }
}
