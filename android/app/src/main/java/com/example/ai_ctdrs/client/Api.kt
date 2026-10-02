package com.example.ai_ctdrs.client

import retrofit2.http.*

data class Token(val access_token: String)
data class Registration(val full_name: String, val email: String, val username: String, val password: String, val confirm_password: String)
data class User(val id: Int, val full_name: String, val username: String, val email: String, val role: String = "analyst", val created_at: String = "")
data class Summary(val eventsAnalyzed: Int, val threatsDetected: Int, val criticalIncidents: Int, val contained: Int)
data class Health(val status: String, val service: String)
data class Telemetry(val destination_port: Int, val flow_duration: Double, val packet_rate: Double, val bytes_rate: Double, val source_ip: String = "192.0.2.10", val destination_ip: String = "198.51.100.20", val protocol: String = "TCP", val is_demo: Boolean = true)
data class Feature(val feature: String, val value: Double, val contribution: Double)
data class Analysis(val id: Int, val classification: String, val confidence: Double, val threat_score: Double, val explanation_json: String, val attack_type: String = "", val timestamp: String = "")
data class Event(val id: Int, val timestamp: String, val is_demo: Boolean, val analysis: Analysis?, val source_ip: String = "", val destination_ip: String = "", val protocol: String = "")
data class ResponseAction(val id: Int, val incident_id: Int?, val action_type: String, val reason: String, val status: String, val timestamp: String)
data class Incident(val id: Int, val analysis_id: Int, val severity: String, val status: String, val created_at: String, val analysis: Analysis?, val responses: List<ResponseAction>)

interface CtdrsApi {
    @FormUrlEncoded @POST("auth/login") suspend fun login(@Field("username") username: String, @Field("password") password: String): Token
    @POST("auth/register") suspend fun register(@Body body: Registration): User
    @GET("auth/me") suspend fun me(): User
    @GET("health") suspend fun health(): Health
    @GET("dashboard/summary") suspend fun summary(): Summary
    @POST("analysis/run") suspend fun analyze(@Body body: Telemetry): Event
    @GET("incidents/") suspend fun incidents(): List<Incident>
    @GET("incidents/{id}") suspend fun incident(@Path("id") id: Int): Incident
    @GET("incidents/responses/recent") suspend fun responses(): List<ResponseAction>
    @GET("events/") suspend fun events(@Query("limit") limit: Int = 20): List<Event>
}

enum class Scenario(val title: String, val telemetry: Telemetry) {
    BENIGN("Benign", Telemetry(443, 100.0, 50.0, 1000.0)),
    PORT_SCAN("Port Scan", Telemetry(32000, 5.0, 500.0, 500.0)),
    BRUTE_FORCE("Brute Force", Telemetry(22, 2000.0, 10.0, 100.0)),
    DDOS("DDoS", Telemetry(80, 50.0, 5000.0, 50000.0)),
    MALWARE("Malware Traffic", Telemetry(4444, 5000.0, 20.0, 200.0))
}
