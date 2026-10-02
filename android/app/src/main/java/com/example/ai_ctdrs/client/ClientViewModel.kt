package com.example.ai_ctdrs.client

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Retrofit
import retrofit2.HttpException
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

data class ClientState(
    val scanning: Boolean = false, val busy: Boolean = true, val error: String? = null, val notice: String? = null, val url: String = SessionStore.PRODUCTION_URL + "/",
    val user: User? = null, val summary: Summary? = null, val health: String = "Not checked",
    val incidents: List<Incident> = emptyList(), val responses: List<ResponseAction> = emptyList(),
    val events: List<Event> = emptyList(), val result: Event? = null, val selected: Incident? = null,
    val protectionEnabled: Boolean = false, val monitorStatus: String = "Paused", val lastMonitorActivityEpochMs: Long? = null
)
class ClientViewModel(app: Application) : AndroidViewModel(app) {
    private val store = SessionStore(app)
    private val mutable = MutableStateFlow(ClientState())
    val state = mutable.asStateFlow()
    private var token: String? = null
    private var scanJob: Job? = null
    private lateinit var api: CtdrsApi
    private fun connect(url: String) {
        val safeUrl = SessionStore.sanitizeServerUrl(url)
        val normalizedUrl = safeUrl.trimEnd('/') + "/"
        val credential = token
        val client = OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).callTimeout(45, TimeUnit.SECONDS)
            .followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                if (credential != null) request.header("Authorization", "Bearer $credential")
                chain.proceed(request.build())
            }.build()
        api = Retrofit.Builder().baseUrl(normalizedUrl).client(client).addConverterFactory(GsonConverterFactory.create()).build().create(CtdrsApi::class.java)
    }
    init {
        viewModelScope.launch { store.protectionState().collect { monitor ->
            mutable.update { it.copy(protectionEnabled = monitor.enabled, monitorStatus = monitor.status, lastMonitorActivityEpochMs = monitor.lastActivityEpochMs) }
        } }
        work {
        val (url, saved) = store.read(); token = saved; connect(url)
        mutable.update { it.copy(url = url) }
        if (token != null) {
            mutable.update { it.copy(user = api.me()) }
            refreshData()
        }
        }
    }
    private fun work(block: suspend () -> Unit): Job = viewModelScope.launch {
        mutable.update { it.copy(busy = true, error = null) }
        try { block() } catch (e: CancellationException) { throw e } catch (e: Exception) {
                if (e is HttpException && e.code() == 401 && token != null) {
                token = null; store.saveToken(null); connect(mutable.value.url)
                mutable.value = ClientState(url = mutable.value.url, error = "Your session has expired. Please sign in again.")
            } else mutable.update { it.copy(error = userMessage(e)) }
        } finally { mutable.update { it.copy(busy = false, scanning = false) } }
    }
    private fun userMessage(error: Exception): String = when (error) {
        is IllegalArgumentException -> error.message ?: "Check the information and try again."
        is SocketTimeoutException -> "The security service is taking too long to respond. Please try again."
        is UnknownHostException, is ConnectException -> "Unable to connect to the security service. Check your internet connection and try again."
        is IOException -> "A network problem interrupted the request. Check your connection and try again."
        is HttpException -> when (error.code()) {
            400 -> when {
                error.response()?.errorBody()?.string()?.contains("already registered", ignoreCase = true) == true -> "An account with this email or username already exists. Try signing in instead."
                else -> "Check the information you entered and try again."
            }
            401 -> "Invalid username or password. Try again."
            403 -> "You do not have permission to complete this request."
            408, 504 -> "The security service is taking too long to respond. Please try again."
            422 -> "The security service could not read this request. Check the fields and try again."
            in 500..599 -> "Security monitoring is temporarily unavailable. We'll retry when you refresh."
            else -> "The security service returned an unexpected response. Please try again."
        }
        else -> "The security service returned an invalid response. Please try again."
    }
    fun authenticate(username: String, password: String, registration: Registration?) { if (state.value.busy) return; work {
        require(username.isNotBlank() && password.isNotBlank()) { "Enter your username and password." }
        if (registration != null) {
            require(registration.password == registration.confirm_password) { "Passwords do not match." }
            api.register(registration)
        }
        token = api.login(username.trim(), password).access_token
        store.saveToken(token); connect(state.value.url)
        mutable.update { it.copy(user = api.me(), notice = if (registration != null) "Account created successfully. Protection is ready." else null) }
        refreshData()
    } }
    private suspend fun refreshData() {
        val health = api.health(); val summary = api.summary(); val incidents = api.incidents()
        val responses = api.responses(); val events = api.events()
        mutable.update { it.copy(health = if (health.status == "ok") "Backend connected" else health.status,
            summary = summary, incidents = incidents, responses = responses, events = events) }
    }
    fun refresh() { if (!state.value.busy) work { refreshData() } }
    fun scan(scenario: Scenario) { if (!state.value.busy) {
        mutable.update { it.copy(result = null, scanning = true, error = null) }
        scanJob = work {
        val result = api.analyze(scenario.telemetry)
        mutable.update { it.copy(result = result) }
        refreshData()
    } } }
    fun stopScan() {
        scanJob?.cancel()
        mutable.update { it.copy(scanning = false, busy = false, notice = "Stopped waiting. The server may still finish this submitted test. Refresh activity before retrying.") }
    }
    fun openIncident(id: Int) { if (!state.value.busy) work { mutable.update { it.copy(selected = null) }; val detail = api.incident(id); mutable.update { it.copy(selected = detail) } } }
    fun closeIncident() { mutable.update { it.copy(selected = null) } }
    fun saveServer(input: String) { if (!state.value.busy) work {
        val trimmed = input.trim()
        val parsed = runCatching { trimmed.toHttpUrl() }.getOrElse { throw IllegalArgumentException("Use a secure production URL for the security service.") }
        require(parsed.username.isEmpty() && parsed.password.isEmpty() && parsed.query == null && parsed.fragment == null) { "Use a server URL without credentials, query or fragment." }
        val host = parsed.host.lowercase()
        require(host !in setOf("10.0.2.2", "localhost", "127.0.0.1", "0.0.0.0", "::1", "[::1]")) { "Local development URLs are not allowed in the production app. Use the secure production service." }
        val url = parsed.toString().trimEnd('/') + "/"
        store.saveUrl(url); token = null; connect(url); mutable.value = ClientState(busy = true, url = url)
        val health = api.health(); mutable.update { it.copy(health = health.service + ": " + health.status) }
    } }
    fun logout() { if (!state.value.busy) work {
        store.setProtection(false)
        getApplication<Application>().stopService(Intent(getApplication(), RuntimeMonitorService::class.java))
        store.saveToken(null); token = null; connect(state.value.url)
        mutable.value = ClientState(busy = true, url = state.value.url)
    } }
    fun enableProtection() {
        if (token == null) return
        viewModelScope.launch {
            store.setProtection(true, "Starting")
            ContextCompat.startForegroundService(getApplication(), Intent(getApplication(), RuntimeMonitorService::class.java))
        }
    }
    fun pauseProtection() = viewModelScope.launch {
        store.setProtection(false)
        getApplication<Application>().stopService(Intent(getApplication(), RuntimeMonitorService::class.java))
    }
}
