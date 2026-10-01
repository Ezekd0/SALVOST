package com.example.ai_ctdrs.data

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
)

data class UserProfile(
    @SerializedName("id") val id: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("created_at") val createdAt: String,
)

data class RegisterRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String = "analyst",
    @SerializedName("password") val password: String,
    @SerializedName("confirm_password") val confirmPassword: String,
)

data class DashboardSummary(
    @SerializedName("eventsAnalyzed") val eventsAnalyzed: Int,
    @SerializedName("threatsDetected") val threatsDetected: Int,
    @SerializedName("criticalIncidents") val criticalIncidents: Int,
    @SerializedName("contained") val contained: Int,
)

data class HealthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("service") val service: String,
)

data class ShapFeature(
    @SerializedName("feature") val feature: String,
    @SerializedName("value") val value: Double,
    @SerializedName("contribution") val contribution: Double,
)

data class AnalysisResult(
    @SerializedName("id") val id: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("threat_score") val threatScore: Double,
    @SerializedName("confidence") val confidence: Double,
    @SerializedName("classification") val classification: String,
    @SerializedName("attack_type") val attackType: String,
    @SerializedName("explanation_json") val explanationJson: String,
    @SerializedName("timestamp") val timestamp: String,
)

data class SecurityEvent(
    @SerializedName("id") val id: Int,
    @SerializedName("source_ip") val sourceIp: String,
    @SerializedName("destination_ip") val destinationIp: String,
    @SerializedName("destination_port") val destinationPort: Int,
    @SerializedName("protocol") val protocol: String,
    @SerializedName("flow_duration") val flowDuration: Double,
    @SerializedName("packet_rate") val packetRate: Double,
    @SerializedName("bytes_rate") val bytesRate: Double,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("is_demo") val isDemo: Boolean = false,
    @SerializedName("analysis") val analysis: AnalysisResult? = null,
)

data class ResponseAction(
    @SerializedName("id") val id: Int,
    @SerializedName("incident_id") val incidentId: Int?,
    @SerializedName("action_type") val actionType: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("status") val status: String,
    @SerializedName("executed_by") val executedBy: Int,
    @SerializedName("timestamp") val timestamp: String,
)

data class Incident(
    @SerializedName("id") val id: Int,
    @SerializedName("analysis_id") val analysisId: Int,
    @SerializedName("severity") val severity: String,
    @SerializedName("status") val status: String,
    @SerializedName("assigned_to") val assignedTo: Int?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("analysis") val analysis: AnalysisResult? = null,
    @SerializedName("responses") val responses: List<ResponseAction> = emptyList(),
)

data class EventRequest(
    @SerializedName("source_ip") val sourceIp: String,
    @SerializedName("destination_ip") val destinationIp: String,
    @SerializedName("destination_port") val destinationPort: Int,
    @SerializedName("protocol") val protocol: String,
    @SerializedName("flow_duration") val flowDuration: Double,
    @SerializedName("packet_rate") val packetRate: Double,
    @SerializedName("bytes_rate") val bytesRate: Double,
    @SerializedName("is_demo") val isDemo: Boolean = true,
)
