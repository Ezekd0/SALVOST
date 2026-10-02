package com.example.ai_ctdrs.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ai_ctdrs.data.AiCtdrsRepository
import com.example.ai_ctdrs.data.AnalysisResult
import com.example.ai_ctdrs.data.EventRequest
import com.example.ai_ctdrs.data.RegisterRequest
import com.example.ai_ctdrs.data.ShapFeature
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AiCtdrsApp(
    viewModel: AppViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.healthCheck()
        viewModel.loadBaseUrl()
    }

    if (!uiState.isAuthenticated) {
        LoginRegisterScreen(viewModel = viewModel, uiState = uiState)
        return
    }

    val tabs = listOf("Dashboard", "Scan", "Threats", "Settings")
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI-CTDRS") },
                actions = {
                    Button(onClick = { viewModel.refreshAccount() }) {
                        Text("Refresh")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = index == selectedTab,
                        onClick = { selectedTab = index },
                        icon = {
                            val icon = when (title) {
                                "Dashboard" -> Icons.Default.Dashboard
                                "Scan" -> Icons.Default.Security
                                "Threats" -> Icons.Default.Notifications
                                else -> Icons.Default.Lock
                            }
                            Icon(icon, contentDescription = title)
                        },
                        label = { Text(title) },
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(viewModel = viewModel, uiState = uiState)
                1 -> ScanScreen(viewModel = viewModel, uiState = uiState)
                2 -> ThreatScreen(viewModel = viewModel, uiState = uiState)
                3 -> SettingsScreen(viewModel = viewModel, uiState = uiState)
            }
        }
    }
}

@Composable
fun LoginRegisterScreen(viewModel: AppViewModel, uiState: AppUiState) {
    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("demo") }
    var password by remember { mutableStateOf("demo123") }
    var fullName by remember { mutableStateOf("Demo Analyst") }
    var email by remember { mutableStateOf("demo@example.com") }
    var confirmPassword by remember { mutableStateOf("demo123") }

    LaunchedEffect(Unit) {
        viewModel.healthCheck()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "AI-CTDRS",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "Protection Command Center",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (uiState.backendStatus != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (uiState.backendStatus.status == "ok") Color(0xFF22C55E) else Color(0xFFEF4444),
                                    CircleShape,
                                ),
                        )
                        Spacer(Modifier.size(8.dp))
                        Text("Backend: ${uiState.backendStatus.status}")
                    }
                }

                if (isLoginMode) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm password") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            if (isLoginMode) {
                                viewModel.login(username.trim(), password.trim())
                            } else {
                                viewModel.register(
                                    RegisterRequest(
                                        fullName = fullName.trim(),
                                        username = username.trim(),
                                        email = email.trim(),
                                        password = password.trim(),
                                        confirmPassword = confirmPassword.trim(),
                                    ),
                                )
                            }
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.weight(1f),
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text(if (isLoginMode) "Sign in" else "Create account")
                        }
                    }
                    OutlinedButton(
                        onClick = { isLoginMode = !isLoginMode },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(if (isLoginMode) "Register" else "Login")
                    }
                }

                Text(
                    text = "Testing telemetry stays explicitly marked as demo traffic.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: AppViewModel, uiState: AppUiState) {
    val summary = uiState.dashboardSummary
    val events = uiState.recentEvents
    val responses = uiState.recentResponses

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Protection Dashboard", style = MaterialTheme.typography.headlineSmall)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(title = "Analyzed", value = (summary?.eventsAnalyzed ?: 0).toString(), color = Color(0xFF2563EB))
                MetricCard(title = "Threats", value = (summary?.threatsDetected ?: 0).toString(), color = Color(0xFFDC2626))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(title = "Critical", value = (summary?.criticalIncidents ?: 0).toString(), color = Color(0xFFF59E0B))
                MetricCard(title = "Contained", value = (summary?.contained ?: 0).toString(), color = Color(0xFF16A34A))
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Recent activity", style = MaterialTheme.typography.titleLarge)
                    if (events.isEmpty()) {
                        Text("No events recorded yet.")
                    } else {
                        events.take(5).forEach { event ->
                            val classification = event.analysis?.classification ?: "Pending"
                            val score = event.analysis?.threatScore ?: 0.0
                            Text("${event.protocol} • ${event.destinationIp} • $classification • ${"%.1f".format(score)}")
                        }
                    }
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Automated responses", style = MaterialTheme.typography.titleLarge)
                    if (responses.isEmpty()) {
                        Text("No response actions recorded.")
                    } else {
                        responses.take(5).forEach { action ->
                            Text("${action.actionType} • ${action.status} • ${action.reason}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScanScreen(viewModel: AppViewModel, uiState: AppUiState) {
    val scenarioOptions = listOf(
        "Normal Traffic" to EventRequest("192.0.2.15", "198.51.100.2", 443, "TCP", 120.0, 18.0, 800.0, true),
        "Port Scan" to EventRequest("192.0.2.16", "198.51.100.2", 40000, "TCP", 8.0, 600.0, 1200.0, true),
        "Brute Force" to EventRequest("192.0.2.17", "198.51.100.2", 22, "TCP", 1800.0, 15.0, 220.0, true),
        "DDoS" to EventRequest("192.0.2.18", "198.51.100.2", 80, "UDP", 50.0, 4900.0, 52000.0, true),
        "Malware Traffic" to EventRequest("192.0.2.19", "198.51.100.2", 8080, "TCP", 5000.0, 22.0, 260.0, true),
    )
    var selectedScenario by remember { mutableStateOf(scenarioOptions.first()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Security Scan", style = MaterialTheme.typography.headlineSmall)
            Text("Controlled test telemetry only. Everything is marked is_demo=true.")
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                scenarioOptions.forEach { (label, _) ->
                    OutlinedButton(
                        onClick = { selectedScenario = scenarioOptions.first { it.first == label } },
                        modifier = Modifier,
                    ) {
                        Text(label)
                    }
                }
            }
        }
        item {
            Button(
                onClick = { viewModel.runSecurityScan(selectedScenario.second) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Run selected scan")
                }
            }
        }
        item {
            val result = uiState.lastScanResult ?: return@item
            val analysis = result.analysis
            if (analysis != null) {
                ResultCard(analysis = analysis, event = result)
            }
        }
    }
}

@Composable
fun ThreatScreen(viewModel: AppViewModel, uiState: AppUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Threats and Incidents", style = MaterialTheme.typography.headlineSmall)
        }
        if (uiState.incidents.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text("No incidents recorded for this account.")
                }
            }
        }
        items(uiState.incidents) { incident ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${incident.severity} • ${incident.status}", style = MaterialTheme.typography.titleMedium)
                    if (incident.analysis != null) {
                        Text("Classification: ${incident.analysis.classification}")
                        Text("Threat score: ${"%.2f".format(incident.analysis.threatScore)}")
                    }
                    incident.responses.forEach { action ->
                        Text("• ${action.actionType}: ${action.reason}")
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: AppViewModel, uiState: AppUiState) {
    var backendUrl by remember { mutableStateOf(uiState.baseUrl) }
    LaunchedEffect(uiState.baseUrl) {
        backendUrl = uiState.baseUrl
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = backendUrl,
                    onValueChange = { backendUrl = it },
                    label = { Text("Backend URL") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {
                        viewModel.setBaseUrl(backendUrl)
                        viewModel.healthCheck()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Save backend URL")
                }
                Text("Current user: ${uiState.user?.username ?: "N/A"}")
                Text("Current token: ${if (uiState.authToken != null) "present" else "missing"}")
                OutlinedButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Log out")
                }
            }
        }
    }
}

@Composable
private fun RowScope.MetricCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun ResultCard(analysis: AnalysisResult, event: com.example.ai_ctdrs.data.SecurityEvent) {
    val shapValues = parseShapFeatures(analysis.explanationJson)
    val severity = severityFromClassification(analysis.classification)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Scan result", style = MaterialTheme.typography.titleLarge)
            Text("Classification: ${analysis.classification}")
            Text("Confidence: ${"%.2f".format(analysis.confidence)}")
            Text("Threat score: ${"%.2f".format(analysis.threatScore)}")
            Text("Severity: $severity")
            Text("Attack type: ${analysis.attackType}")
            Text("Source: ${event.sourceIp} → ${event.destinationIp}:${event.destinationPort}")
            Text("Demo telemetry: ${event.isDemo}")

            if (shapValues.isNotEmpty()) {
                Text("SHAP feature importance", style = MaterialTheme.typography.titleMedium)
                shapValues.take(4).forEach { feature ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${feature.feature}: ${"%.3f".format(feature.contribution)}")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                    RoundedCornerShape(50),
                                ),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(feature.contribution.coerceIn(-1.0, 1.0).let { if (it < 0) 0.15f else ((it + 1.0) / 2.0).toFloat() })
                                    .height(8.dp)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50)),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun parseShapFeatures(raw: String): List<ShapFeature> {
    if (raw.isBlank()) return emptyList()
    return try {
        val type: Type = object : TypeToken<List<ShapFeature>>() {}.type
        Gson().fromJson(raw, type)
    } catch (_: Exception) {
        emptyList()
    }
}

private fun severityFromClassification(classification: String): String = when (classification) {
    "Benign" -> "Low"
    "Port Scan" -> "Moderate"
    "Brute Force" -> "High"
    "DDoS", "Malware Traffic" -> "Critical"
    else -> "Unknown"
}
