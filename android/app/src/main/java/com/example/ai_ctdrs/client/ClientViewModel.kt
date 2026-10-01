package com.example.ai_ctdrs.client

import android.app.Application
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
import java.util.concurrent.TimeUnit

data class ClientState(
    val scanning: Boolean = false, val busy: Boolean = true, val error: String? = null, val url: String = "http://10.0.2.2:8000/",
    val user: User? = null, val summary: Summary? = null, val health: String = "Not checked",
    val incidents: List<Incident> = emptyList(), val responses: List<ResponseAction> = emptyList(),
    val events: List<Event> = emptyList(), val result: Event? = null, val selected: Incident? = null
)
class ClientViewModel(app: Application) : AndroidViewModel(app) {
    private val store = SessionStore(app)
    private val mutable = MutableStateFlow(ClientState())
    val state = mutable.asStateFlow()
    private var token: String? = null
    private var scanJob: Job? = null
    private lateinit var api: CtdrsApi
    private fun connect(url: String) {
        // Capture credentials per client: changing servers cannot forward a previous server's token.
        val credential = token
        val client = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
            .followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                if (credential != null) request.header("Authorization", "Bearer $credential")
                chain.proceed(request.build())
            }.build()
        api = Retrofit.Builder().baseUrl(url).client(client).addConverterFactory(GsonConverterFactory.create()).build().create(CtdrsApi::class.java)
    }
    init { work {
        val (url, saved) = store.read(); token = saved; connect(url)
        mutable.update { it.copy(url = url) }
        if (token != null) { mutable.update { it.copy(user = api.me()) }; refreshData() }
    } }
    private fun work(block: suspend () -> Unit): Job = viewModelScope.launch {
        mutable.update { it.copy(busy = true, error = null) }
        try { block() } catch (e: CancellationException) { throw e } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                token = null; store.saveToken(null); connect(mutable.value.url)
                mutable.value = ClientState(busy = true, url = mutable.value.url, error = "Sign in again. Your session expired or credentials were incorrect.")
            } else mutable.update { it.copy(error = when (e) {
                is HttpException -> "Server returned ${e.code()}: ${e.response()?.errorBody()?.string()?.take(250) ?: e.message()}"
                else -> e.message ?: "Connection failed. Check the server address and try again."
            }) }
        } finally { mutable.update { it.copy(busy = false, scanning = false) } }
    }
    fun authenticate(username: String, password: String, registration: Registration?) { if (state.value.busy) return; work {
        require(username.isNotBlank() && password.isNotBlank()) { "Enter your username and password." }
        if (registration != null) {
            require(registration.password == registration.confirm_password) { "Passwords do not match." }
            api.register(registration)
        }
        token = api.login(username.trim(), password).access_token
        store.saveToken(token); connect(state.value.url)
        mutable.update { it.copy(user = api.me()) }; refreshData()
    } }
    private suspend fun refreshData() {
        val health = api.health(); val summary = api.summary(); val incidents = api.incidents()
        val responses = api.responses(); val events = api.events()
        mutable.update { it.copy(health = if (health.status == "ok") "Backend connected" else health.status,
            summary = summary, incidents = incidents, responses = responses, events = events) }
    }
    fun refresh() { if (!state.value.busy) work { refreshData() } }
    fun scan(scenario: Scenario) { if (!state.value.busy) {
        mutable.update { it.copy(result = null, scanning = true) }
        scanJob = work {
        val result = api.analyze(scenario.telemetry)
        mutable.update { it.copy(result = result) }
        refreshData()
    } } }
    fun stopScan() {
        scanJob?.cancel()
        mutable.update { it.copy(scanning = false, busy = false, error = "Stopped waiting. The server may still finish this submitted test. Refresh activity before retrying.") }
    }
    fun openIncident(id: Int) { if (!state.value.busy) work { mutable.update { it.copy(selected = null) }; val detail = api.incident(id); mutable.update { it.copy(selected = detail) } } }
    fun closeIncident() { mutable.update { it.copy(selected = null) } }
    fun saveServer(input: String) { if (!state.value.busy) work {
        val parsed = input.trim().toHttpUrl()
        require(parsed.username.isEmpty() && parsed.password.isEmpty() && parsed.query == null && parsed.fragment == null) { "Use a server URL without credentials, query or fragment." }
        val url = parsed.toString().trimEnd('/') + "/"
        store.saveUrl(url); token = null; connect(url); mutable.value = ClientState(busy = true, url = url)
        val health = api.health(); mutable.update { it.copy(health = health.service + ": " + health.status) }
    } }
    fun logout() { if (!state.value.busy) work {
        store.saveToken(null); token = null; connect(state.value.url)
        mutable.value = ClientState(busy = true, url = state.value.url)
    } }
}
