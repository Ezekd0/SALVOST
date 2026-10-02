package com.example.ai_ctdrs.client

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ai_ctdrs.theme.*
import java.text.DateFormat
import java.util.Date

@Composable internal fun Dashboard(s: ClientState, vm: ClientViewModel, navigate: (String)->Unit) {
    val context = LocalContext.current
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { vm.enableProtection() }
    Heading("Protection Status")
    Panel {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            val active = s.protectionEnabled
            Symbol(if(active) Icons.Outlined.VerifiedUser else Icons.Outlined.PauseCircleOutline, if(active) Green else Amber, 56)
            Column(Modifier.weight(1f)) { Text(if(active) "Protection Active" else "Protection Paused", style=MaterialTheme.typography.titleMedium); Caption(if(active) "${s.monitorStatus}. Monitoring this app's runtime network signals." else "Enable protection to start runtime security monitoring.") }
        }
    }
    if (s.protectionEnabled) {
        Caption(s.lastMonitorActivityEpochMs?.let { "Last activity: ${DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(it))}" } ?: "Monitoring is active. No security events detected yet.")
    }
    Caption("Runtime monitoring measures this app's own network byte and packet counters. It does not inspect packets, scan other apps, or provide full-device antivirus coverage.")
    if (s.protectionEnabled) Primary("Pause Protection", !s.busy) { vm.pauseProtection() } else Primary("Enable Protection", !s.busy) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS) else vm.enableProtection()
    }
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) { Metric("Threat alerts",s.summary?.threatsDetected?.toString() ?: "—","For this account",Icons.Outlined.GppMaybe,Red,Modifier.weight(1f)); Metric("Contained",s.summary?.contained?.toString() ?: "—","Server responses",Icons.Outlined.Shield,Green,Modifier.weight(1f)) }
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) { Metric("Recent incidents",if(s.summary!=null) s.incidents.size.toString() else "—","For this account",Icons.Outlined.WarningAmber,Amber,Modifier.weight(1f)); Metric("Connection",if(s.health=="Backend connected") "Online" else "Check","Security service",Icons.Outlined.CloudDone,Blue,Modifier.weight(1f)) }
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) { Text("Recent Security Events",style=MaterialTheme.typography.titleMedium); TextButton(onClick={navigate("Events")}) {Text("View all")} }
    ActivityList(s.events.take(3))
    Primary("Run a controlled detection test",!s.busy) {navigate("Testing")}; TextButton(onClick=vm::refresh,enabled=!s.busy) {Icon(Icons.Outlined.Refresh,null); Text("Refresh protection status")}
}
@Composable private fun Metric(label:String,value:String,detail:String,icon:ImageVector,color:Color,modifier:Modifier) { Panel(modifier) { Symbol(icon,color); Text(value,style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.SemiBold); Text(label,style=MaterialTheme.typography.bodyMedium); Text(detail,color=Muted,style=MaterialTheme.typography.labelSmall) } }
@Composable internal fun EmptyState(title:String,body:String) { Panel { Symbol(Icons.Outlined.Shield); Text(title,style=MaterialTheme.typography.titleMedium); Caption(body) } }
@Composable internal fun ActivityList(events:List<Event>) {
    if(events.isEmpty()) EmptyState("No activity yet","Your submitted analyses will appear here after a scan.")
    events.forEach { event -> Panel { Row(horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically) { Symbol(Icons.Outlined.Security,if(event.analysis?.classification=="Benign") Green else Red); Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)) { Text(event.analysis?.classification ?: "Awaiting analysis",fontWeight=FontWeight.Medium); Caption(event.timestamp); Badge(if(event.is_demo) "Controlled test" else "Recorded event") } } } }
}
@Composable internal fun ReportsScreen(s:ClientState,vm:ClientViewModel) {
    val context=LocalContext.current
    Badge("All-time account summary"); Caption("Current totals returned by the server. Historical trends and a system-health percentage are not provided.")
    listOf(Triple("Total threats",s.summary?.threatsDetected, Violet),Triple("Critical incidents",s.summary?.criticalIncidents,Amber),Triple("Contained incidents",s.summary?.contained,Green),Triple("Events analyzed",s.summary?.eventsAnalyzed,Blue)).forEach { (label,value,color) -> Panel { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) { Column { Caption(label); Text(value?.toString() ?: "—",style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.SemiBold) }; Symbol(Icons.Outlined.Assessment,color,56) } } }
    Primary("Share account summary",s.summary!=null) {
        val v=s.summary ?: return@Primary
        val text="AI-CTDRS — Account summary\nAccount: ${s.user?.username}\nEvents analyzed: ${v.eventsAnalyzed}\nThreats detected: ${v.threatsDetected}\nCritical incidents: ${v.criticalIncidents}\nContained incidents: ${v.contained}\nSource: current backend account totals. Includes controlled test telemetry. No historical trends or device-health measurement."
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {type="text/plain";putExtra(Intent.EXTRA_TEXT,text)},"Share account summary"))
    }
    TextButton(onClick=vm::refresh,enabled=!s.busy) {Text("Refresh report data")}
}
@Composable internal fun ProfileScreen(s:ClientState) {
    val user=s.user ?: return
    Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)) { Surface(shape=CircleShape,color=Violet,modifier=Modifier.size(94.dp)) {Box(contentAlignment=Alignment.Center) {Text(initials(user.full_name),style=MaterialTheme.typography.headlineLarge)}}; Heading(user.full_name); Caption(user.email) }
    Spacer(Modifier.height(12.dp))
    listOf("Full Name" to user.full_name,"Email" to user.email,"Username" to user.username,"Member Since" to user.created_at.ifBlank {"Not provided"}).forEach { (label,value) -> Panel {Caption(label);Text(value)} }
    Caption("Profile editing is not available through the current service.")
}
@Composable internal fun SettingsScreen(s:ClientState,navigate:(String)->Unit) {
    listOf("Account" to listOf("Profile","Security","Notifications"),"Application" to listOf("Scan Settings","AI Model Settings","Data & Storage","Language")).forEach { (section,items) ->
        Text(section,style=MaterialTheme.typography.titleMedium)
        items.forEach { title -> val subtitle=when(title) {"Profile"->"Your account details";"Security"->"Session and authentication";"Notifications"->"Activity and notification availability";"Scan Settings"->"Controlled test scenarios";"AI Model Settings"->"Server-managed detection model";"Data & Storage"->"Storage and server connection";else->"English"}
            Card(onClick={navigate(title)},colors=CardDefaults.cardColors(containerColor=com.example.ai_ctdrs.theme.Panel),modifier=Modifier.fillMaxWidth()) {Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {Symbol(when(title){"Profile"->Icons.Outlined.Person;"Security"->Icons.Outlined.Lock;"Notifications"->Icons.Outlined.NotificationsNone;"Language"->Icons.Outlined.Language;"Data & Storage"->Icons.Outlined.Storage;else->Icons.Outlined.Settings}); Column(Modifier.weight(1f)){Text(title);Caption(subtitle)};Icon(Icons.Outlined.ChevronRight,null,tint=Muted)}}
        }
    }
}
@Composable internal fun IncidentsScreen(s:ClientState,vm:ClientViewModel,navigate:(String)->Unit) {
    var filter by rememberSaveable {mutableStateOf("All")}
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) { (listOf("All","Open","Contained","Investigating","Resolved") + s.incidents.map{it.status}).distinct().forEach { status -> FilterChip(selected=filter==status,onClick={filter=status},label={Text(status)}) } }
    val incidents=s.incidents.filter {filter=="All" || it.status.equals(filter,true)}
    if(incidents.isEmpty()) EmptyState("No ${if(filter=="All") "" else "$filter "}incidents","Incident records appear here when the backend creates them from analysis.")
    incidents.forEach { incident -> Card(onClick={vm.openIncident(incident.id);navigate("Incident Detail")},enabled=!s.busy,colors=CardDefaults.cardColors(containerColor=com.example.ai_ctdrs.theme.Panel),modifier=Modifier.fillMaxWidth()) {Row(Modifier.padding(16.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically) {Symbol(Icons.Outlined.GppMaybe,severityColor(incident.severity)); Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(6.dp)) {Text(incident.analysis?.classification ?: "Incident #${incident.id}",fontWeight=FontWeight.SemiBold); Caption(incident.created_at); Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){Badge(incident.severity,severityColor(incident.severity));Badge(incident.status,severityColor(incident.status))}};Icon(Icons.Outlined.ChevronRight,null,tint=Muted)} } }
    TextButton(onClick=vm::refresh,enabled=!s.busy){Icon(Icons.Outlined.Refresh,null);Text("Refresh incidents")}
    Responses(s.responses)
}
@Composable internal fun Responses(responses:List<ResponseAction>) {
    Text("Automated responses",style=MaterialTheme.typography.titleMedium); Caption("Responses to controlled test telemetry are simulated; they do not change phone apps or firewall rules.")
    if(responses.isEmpty()) Caption("No response records available.")
    responses.forEach { response -> Panel {Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(response.action_type,modifier=Modifier.weight(1f));Badge(response.status,Green)};Caption(response.reason);Caption(response.timestamp)} }
}
@Composable internal fun IncidentDetails(s:ClientState,vm:ClientViewModel) {
    val incident=s.selected
    if(incident==null) {EmptyState(if(s.busy) "Loading incident" else "Incident unavailable","Select an incident or retry after refreshing the incident list.");return}
    Symbol(Icons.Outlined.GppMaybe,severityColor(incident.severity),64);Heading(incident.analysis?.classification ?: "Incident #${incident.id}")
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Badge(incident.status,severityColor(incident.status));Badge(incident.severity,severityColor(incident.severity))}
    Panel {Caption("Incident #${incident.id} • Analysis #${incident.analysis_id}");Text("Detected");Caption(incident.created_at);val event=s.events.find {it.analysis?.id==incident.analysis_id};if(event!=null){Badge(if(event.is_demo) "Controlled test telemetry" else "Recorded event");Caption("Source: ${event.source_ip}\nDestination: ${event.destination_ip}")}}
    Responses(incident.responses)
    incident.analysis?.let {Explanation(it)}
    Caption("Responses are executed automatically by the backend. Manual actions and Mark as Resolved are not supported by this API.")
    Primary("Refresh incident",!s.busy){vm.openIncident(incident.id)}
}
