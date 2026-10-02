package com.example.ai_ctdrs.client

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ai_ctdrs.theme.*
import kotlinx.coroutines.delay

private val primaryPages = listOf("Home", "Events", "Alerts", "Analytics", "Settings")

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ClientScreen(vm: ClientViewModel = viewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("presentation", 0) }
    var intro by rememberSaveable { mutableStateOf(if (prefs.getBoolean("onboarded", false)) -1 else 0) }
    var splash by rememberSaveable { mutableStateOf(true) }
    var page by rememberSaveable { mutableStateOf("Home") }
    val navigate: (String) -> Unit = { page = it }

    LaunchedEffect(Unit) { delay(1300); splash = false }
    LaunchedEffect(s.user?.id) { if (s.user == null) page = "Home" }
    BackHandler(page !in primaryPages) { page = parentPage(page); vm.closeIncident() }

    if (splash) {
        Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
            Waves(Modifier.fillMaxSize())
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SecurityArt(Modifier.size(145.dp), "logo")
                Heading("AI-DRIVEN ANDROID RUNTIME THREAT DETECTION AND AUTONOMOUS RESPONSE SYSTEM")
                Spacer(Modifier.height(12.dp))
                Caption("Protection and account-scoped security insights")
            }
            Text("Powered by AI for a safer digital world", color = Muted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp))
        }
        return
    }
    if (intro >= 0 && s.user == null) {
        Onboarding(intro, { intro++ }, { intro = -1; prefs.edit().putBoolean("onboarded", true).apply() })
        return
    }
    if (s.user == null) { AuthScreen(s, vm); return }

    Scaffold(
        containerColor = Night,
        topBar = {
            if (page != "Testing in progress") {
                TopAppBar(
                    title = { if (page == "Home") Brand() else Text(pageTitle(page), style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        if (page !in primaryPages) IconButton(onClick = { page = parentPage(page); vm.closeIncident() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Night),
                )
            }
        },
        bottomBar = {
            if (page in primaryPages) {
                NavigationBar(containerColor = Panel, tonalElevation = 0.dp) {
                    primaryPages.forEach { name ->
                        NavigationBarItem(
                            selected = page == name,
                            onClick = { navigate(name) },
                            icon = { Icon(pageIcon(name), null) },
                            label = { Text(if (name == "Home") "Protection" else name) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFB79BFF), selectedTextColor = Color(0xFFB79BFF),
                                indicatorColor = Violet.copy(alpha = .16f), unselectedIconColor = Muted, unselectedTextColor = Muted,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
            if (s.busy && page != "Testing in progress") LinearProgressIndicator(Modifier.fillMaxWidth(), color = Violet)
            s.error?.let { Text(it, color = Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) }
            s.notice?.let { Text(it, color = Green, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) }
            Crossfade(page, label = "Screen transition") { current ->
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    when (current) {
                        "Home" -> Dashboard(s, vm, navigate)
                        "Events" -> { Heading("Security Events"); Caption("Events recorded for this account. Controlled tests are marked clearly."); ActivityList(s.events); Responses(s.responses) }
                        "Alerts" -> { Heading("Threat Alerts"); Caption("Threats and incidents associated with this account."); IncidentsScreen(s, vm, navigate) }
                        "Analytics" -> { Heading("Your Security Summary"); ReportsScreen(s, vm) }
                        "Settings" -> SettingsScreen(s, navigate)
                        "Testing" -> ScanScreen(s) { scenario -> vm.scan(scenario); navigate("Testing in progress") }
                        "Testing in progress" -> ScanningScreen(s, { vm.stopScan(); navigate("Testing") }, navigate)
                        "Test Results" -> ResultScreen(s, navigate, vm)
                        "Threat Details" -> ThreatDetails(s, navigate, vm)
                        "Incident Detail" -> IncidentDetails(s, vm)
                        "Profile" -> ProfileScreen(s)
                        "Server" -> ServerScreen(s, vm)
                        "Security" -> AccountSecurity(vm)
                        "Notifications" -> NotificationStatus(s)
                        "Scan Settings" -> TestingInfo(navigate)
                        "AI Model Settings" -> ModelInfo()
                        "Data & Storage" -> StorageInfo(navigate)
                        "Language" -> { Heading("Language"); Panel { Text("English"); Caption("The currently supported application language.") } }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable private fun AccountSecurity(vm: ClientViewModel) {
    Heading("Account Security")
    Panel { Caption("Your sign-in token is encrypted using Android Keystore. Sessions are validated by the server."); Caption("Password changes and two-factor authentication are not available in the current service.") }
    Primary("Sign out", click = vm::logout)
}

@Composable private fun NotificationStatus(s: ClientState) {
    Heading("Alerts")
    Panel { Text("Runtime monitoring alerts"); Caption(if (s.protectionEnabled) "The foreground monitoring service can post an alert when the backend assesses a recorded runtime signal as suspicious." else "Enable Protection from the Home screen to receive runtime-monitoring alerts.") }
    ActivityList(s.events)
}

@Composable private fun TestingInfo(navigate: (String) -> Unit) {
    Heading("Controlled Testing")
    Panel { Caption("Five predefined traffic profiles can be submitted to the existing analysis service for demonstration and testing. They do not scan this phone or create real network traffic.") }
    Primary("Choose a test scenario") { navigate("Testing") }
}

@Composable private fun ModelInfo() {
    Heading("AI Detection")
    Panel { Text("Random Forest • SHAP explainability"); Caption("Inference and model configuration are managed by the backend. This app does not modify model parameters.") }
}

@Composable private fun StorageInfo(navigate: (String) -> Unit) {
    Heading("Data & Storage")
    Panel { Caption("Events, incidents, and responses are stored by the backend for this account. Your session is stored encrypted on this device."); Caption("Server data deletion is not exposed by the current API.") }
    Primary("Server connection") { navigate("Server") }
}

internal fun initials(name: String) = name.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() }.ifBlank { "U" }
internal fun pageIcon(name: String) = when (name) {
    "Home" -> Icons.Outlined.Home
    "Events" -> Icons.Outlined.History
    "Alerts" -> Icons.Outlined.GppMaybe
    "Analytics" -> Icons.Outlined.Assessment
    "Testing" -> Icons.Outlined.Science
    "Settings" -> Icons.Outlined.Settings
    else -> Icons.Outlined.MoreHoriz
}
private fun pageTitle(page: String) = when (page) { "Home" -> "Protection"; else -> page }
internal fun parentPage(page: String) = when (page) {
    "Incident Detail" -> "Alerts"
    "Threat Details" -> "Test Results"
    "Test Results", "Testing in progress" -> "Testing"
    "Security", "Notifications", "Scan Settings", "AI Model Settings", "Data & Storage", "Language", "Server", "Profile" -> "Settings"
    else -> "Home"
}
