package com.example.ai_ctdrs.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_ctdrs.data.AiCtdrsRepository
import com.example.ai_ctdrs.data.DashboardSummary
import com.example.ai_ctdrs.data.EventRequest
import com.example.ai_ctdrs.data.HealthResponse
import com.example.ai_ctdrs.data.Incident
import com.example.ai_ctdrs.data.RegisterRequest
import com.example.ai_ctdrs.data.ResponseAction
import com.example.ai_ctdrs.data.SecurityEvent
import com.example.ai_ctdrs.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AiCtdrsRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        loadSavedSession()
    }

    fun loadSavedSession() {
        viewModelScope.launch {
            val savedToken = repository.getToken()
            if (savedToken.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(isAuthenticated = false, isLoading = false)
                return@launch
            }
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, authToken = savedToken)
                val user = repository.getCurrentUser(savedToken)
                val summary = repository.getDashboardSummary(savedToken)
                val incidents = repository.getIncidents(savedToken)
                val responses = repository.getRecentResponses(savedToken)
                val events = repository.getEvents(savedToken, 10)
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    isLoading = false,
                    user = user,
                    authToken = savedToken,
                    dashboardSummary = summary,
                    incidents = incidents,
                    recentResponses = responses,
                    recentEvents = events,
                    errorMessage = null,
                )
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = false,
                    isLoading = false,
                    errorMessage = "Session expired or backend unavailable.",
                )
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                val user = repository.login(username, password)
                val token = repository.getToken() ?: throw IllegalStateException("No token was saved.")
                val summary = repository.getDashboardSummary(token)
                val incidents = repository.getIncidents(token)
                val responses = repository.getRecentResponses(token)
                val events = repository.getEvents(token, 10)
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    isLoading = false,
                    user = user,
                    authToken = token,
                    dashboardSummary = summary,
                    incidents = incidents,
                    recentResponses = responses,
                    recentEvents = events,
                    errorMessage = null,
                )
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Login request failed.",
                    isAuthenticated = false,
                )
            }
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                val user = repository.register(request)
                val password = request.password
                login(user.username, password)
                _uiState.value = _uiState.value.copy(user = user)
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Registration failed.",
                )
            }
        }
    }

    fun refreshAccount() {
        viewModelScope.launch {
            val token = _uiState.value.authToken ?: repository.getToken() ?: return@launch
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val user = repository.getCurrentUser(token)
                val summary = repository.getDashboardSummary(token)
                val incidents = repository.getIncidents(token)
                val responses = repository.getRecentResponses(token)
                val events = repository.getEvents(token, 10)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = user,
                    dashboardSummary = summary,
                    incidents = incidents,
                    recentResponses = responses,
                    recentEvents = events,
                    errorMessage = null,
                )
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Unable to refresh account.",
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearToken()
            _uiState.value = AppUiState()
        }
    }

    fun setBaseUrl(value: String) {
        viewModelScope.launch {
            repository.setSavedBaseUrl(value)
            _uiState.value = _uiState.value.copy(baseUrl = value)
        }
    }

    fun loadBaseUrl() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(baseUrl = repository.getSavedBaseUrl())
        }
    }

    fun runSecurityScan(request: EventRequest) {
        viewModelScope.launch {
            val token = _uiState.value.authToken ?: repository.getToken() ?: return@launch
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                val event = repository.runAnalysis(token, request)
                val incidents = repository.getIncidents(token)
                val responses = repository.getRecentResponses(token)
                val events = repository.getEvents(token, 10)
                val summary = repository.getDashboardSummary(token)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastScanResult = event,
                    incidents = incidents,
                    recentResponses = responses,
                    recentEvents = events,
                    dashboardSummary = summary,
                )
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Scan failed.",
                )
            }
        }
    }

    fun healthCheck() {
        viewModelScope.launch {
            try {
                val result = repository.healthCheck()
                _uiState.value = _uiState.value.copy(backendStatus = result)
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    backendStatus = HealthResponse(status = "unavailable", service = "AI-CTDRS Backend"),
                )
            }
        }
    }
}

data class AppUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val authToken: String? = null,
    val user: UserProfile? = null,
    val baseUrl: String = AiCtdrsRepository.DEFAULT_BASE_URL,
    val dashboardSummary: DashboardSummary? = null,
    val incidents: List<Incident> = emptyList(),
    val recentResponses: List<ResponseAction> = emptyList(),
    val recentEvents: List<SecurityEvent> = emptyList(),
    val lastScanResult: SecurityEvent? = null,
    val backendStatus: HealthResponse? = null,
    val errorMessage: String? = null,
)
