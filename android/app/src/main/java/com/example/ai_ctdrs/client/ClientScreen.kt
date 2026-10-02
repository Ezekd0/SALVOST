package com.example.ai_ctdrs.client

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.Icons
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ClientScreen(vm: ClientViewModel = viewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("presentation", 0) }
    var intro by rememberSaveable { mutableStateOf(if(prefs.getBoolean("onboarded",false)) -1 else 0) }
    var splash by rememberSaveable { mutableStateOf(true) }
    var page by rememberSaveable { mutableStateOf("Home") }
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navigate: (String) -> Unit = { page = it }
    LaunchedEffect(Unit) { delay(1300); splash = false }
    LaunchedEffect(s.user?.id) { if(s.user == null) { page = "Home" } }
    BackHandler(drawer.isOpen || page != "Home") { if(drawer.isOpen) scope.launch { drawer.close() } else { page = parentPage(page); vm.closeIncident() } }
    if(splash) { Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) { Waves(Modifier.fillMaxSize()); Column(horizontalAlignment = Alignment.CenterHorizontally) { SecurityArt(Modifier.size(145.dp), "logo"); Heading("AI-DRIVEN ANDROID RUNTIME THREAT DETECTION AND AUTONOMOUS RESPONSE SYSTEM"); Spacer(Modifier.height(12.dp)); Caption("AI-DRIVEN ANDROID RUNTIME THREAT DETECTION AND AUTONOMOUS RESPONSE SYSTEM") }; Text("Powered by AI for a safer digital world", color = Muted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)) }; return }
    if(intro >= 0 && s.user == null) { Onboarding(intro, { intro++ }, { intro = -1; prefs.edit().putBoolean("onboarded",true).apply() }); return }
    if(s.user == null) { AuthScreen(s, vm); return }
    ModalNavigationDrawer(drawerState = drawer, drawerContent = {
        ModalDrawerSheet(drawerContainerColor = Night, modifier = Modifier.widthIn(max = 310.dp)) {
            Spacer(Modifier.height(28.dp)); Box(Modifier.padding(24.dp)) { Brand() }; Spacer(Modifier.height(18.dp))
            listOf("Home", "Scan", "Incidents", "Reports", "Threat Intelligence", "Settings").forEach { name -> NavigationDrawerItem(label = { Text(if(name == "Home") "Dashboard" else name) }, selected = page == name, icon = { Icon(pageIcon(name), null) }, onClick = { navigate(name); scope.launch { drawer.close() } }, modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp), colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = Violet, selectedTextColor = Color.White, selectedIconColor = Color.White)) }
            Spacer(Modifier.weight(1f)); TextButton(onClick = { scope.launch { drawer.close() }; vm.logout() }, enabled = !s.busy, modifier = Modifier.padding(24.dp)) { Icon(Icons.AutoMirrored.Outlined.Logout, null, tint = Red); Spacer(Modifier.width(12.dp)); Text("Logout",color=Color.White) }
        }
    }) {
        Scaffold(containerColor = Night, topBar = {
            if(page != "Scanning") TopAppBar(title = { if(page == "Home") Brand() else Text(page, style = MaterialTheme.typography.titleLarge) }, navigationIcon = {
                IconButton(onClick = { if(page in listOf("Home","Incidents","Scan","Reports","More")) scope.launch { drawer.open() } else { page = parentPage(page); vm.closeIncident() } }) { Icon(if(page in listOf("Home","Incidents","Scan","Reports","More")) Icons.Outlined.Menu else Icons.AutoMirrored.Outlined.ArrowBack, "Navigate") }
            }, actions = { if(page=="Home") { IconButton(onClick = { navigate("Activity") }) { Icon(Icons.Outlined.NotificationsNone,"Recent activity") }; IconButton(onClick = { navigate("Profile") }) { Surface(shape=CircleShape,color=Violet.copy(alpha=.25f)) { Text(initials(s.user?.full_name.orEmpty()),Modifier.padding(9.dp),color=Color(0xFFB89AFF)) } } } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Night))
        }, bottomBar = {
            if(page in listOf("Home","Scan","Incidents","Reports","More")) NavigationBar(containerColor = com.example.ai_ctdrs.theme.Panel, tonalElevation = 0.dp) { listOf("Home","Scan","Incidents","Reports","More").forEach { name -> NavigationBarItem(selected=page==name,onClick={navigate(name)},icon={Icon(pageIcon(name),null)},label={Text(name)},colors=NavigationBarItemDefaults.colors(selectedIconColor=Color(0xFFB79BFF),selectedTextColor=Color(0xFFB79BFF),indicatorColor=Violet.copy(alpha=.16f),unselectedIconColor=Muted,unselectedTextColor=Muted)) } }
        }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
                if(s.busy && page != "Scanning") LinearProgressIndicator(Modifier.fillMaxWidth(),color=Violet)
                s.error?.let { Text(it,color=Red,style=MaterialTheme.typography.bodySmall,modifier=Modifier.padding(horizontal=20.dp,vertical=8.dp)) }
                Crossfade(page, label="Screen transition") { current ->
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
                        when(current) {
                            "Home" -> Dashboard(s,vm,navigate)
                            "Scan" -> ScanScreen(s) { scenario -> vm.scan(scenario); navigate("Scanning") }
                            "Scanning" -> ScanningScreen(s, { vm.stopScan(); navigate("Scan") }, { navigate(it) })
                            "Scan Results" -> ResultScreen(s,navigate,vm)
                            "Threat Details" -> ThreatDetails(s,navigate,vm)
                            "Incidents" -> IncidentsScreen(s,vm,navigate)
                            "Incident Detail" -> IncidentDetails(s,vm)
                            "Reports" -> ReportsScreen(s,vm)
                            "Profile" -> ProfileScreen(s)
                            "Settings", "More" -> SettingsScreen(s,navigate)
                            "Server" -> ServerScreen(s,vm)
                            "Activity" -> { Heading("Recent activity"); ActivityList(s.events); Responses(s.responses) }
                            "Threat Intelligence" -> { Heading("Detection intelligence"); Caption("Classification and SHAP explanations from your recorded analyses."); if(s.events.isEmpty()) EmptyState("No analyses yet", "Run a controlled scan to inspect the model's findings."); s.events.forEach { event -> Panel { Text(event.analysis?.classification ?: "Pending analysis"); Badge(if(event.is_demo) "Controlled test" else "Recorded event"); Caption(event.timestamp); event.analysis?.let { Explanation(it) } } } }
                            "Security" -> { Heading("Account security"); Panel { Caption("Your sign-in token is encrypted using Android Keystore. Sessions are validated by the server."); Caption("Password changes and two-factor authentication are not available in the current service.") }; Primary("Sign out",!s.busy,vm::logout) }
                            "Notifications" -> { Heading("Notifications"); Caption("Push notifications are not supported by this version. Recent server activity is available below."); ActivityList(s.events) }
                            "Scan Settings" -> { Heading("Controlled scan settings"); Caption("Five predefined traffic profiles are submitted to the existing analysis service. Device scanning and scheduled monitoring are not enabled."); Primary("Choose a test scenario") { navigate("Scan") } }
                            "AI Model Settings" -> { Heading("AI detection"); Panel { Text("Random Forest • SHAP explainability"); Caption("Inference and model configuration are managed by the backend. This app does not modify model parameters.") } }
                            "Data & Storage" -> { Heading("Data & Storage"); Panel { Caption("Events, incidents and responses are stored by the backend. Your session is stored encrypted on this device."); Caption("Server data deletion is not exposed by the current API.") }; Primary("Server connection") { navigate("Server") } }
                            "Language" -> { Heading("Language"); Panel { Text("English"); Caption("The currently supported application language.") } }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
internal fun initials(name: String) = name.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() }.ifBlank { "U" }
internal fun pageIcon(name: String) = when(name) { "Home" -> Icons.Outlined.Home; "Scan" -> Icons.Outlined.Radar; "Incidents" -> Icons.Outlined.GppMaybe; "Reports" -> Icons.Outlined.Assessment; "Threat Intelligence" -> Icons.Outlined.Shield; "Settings" -> Icons.Outlined.Settings; else -> Icons.Outlined.MoreHoriz }

internal fun parentPage(page: String) = when(page) { "Incident Detail" -> "Incidents"; "Threat Details" -> "Scan Results"; "Scan Results", "Scanning" -> "Scan"; "Security", "Notifications", "Scan Settings", "AI Model Settings", "Data & Storage", "Language", "Server", "Profile" -> "Settings"; else -> "Home" }
