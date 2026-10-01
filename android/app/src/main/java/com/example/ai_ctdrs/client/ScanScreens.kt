package com.example.ai_ctdrs.client

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ai_ctdrs.theme.*
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable internal fun ScanScreen(s:ClientState,start:(Scenario)->Unit) {
    var selected by rememberSaveable {mutableStateOf(Scenario.BENIGN)};var expanded by remember {mutableStateOf(false)}
    Heading("Threat Scan");Caption("Analyze a controlled traffic profile using AI detection.")
    SecurityArt(Modifier.fillMaxWidth().height(260.dp),"radar")
    Panel {Caption("Scan type");Box {TextButton(onClick={expanded=true},enabled=!s.busy,modifier=Modifier.fillMaxWidth()){Text(if(selected==Scenario.BENIGN) "Normal Traffic / Benign" else selected.title,modifier=Modifier.weight(1f));Icon(Icons.Outlined.ExpandMore,"Choose scenario")};DropdownMenu(expanded=expanded,onDismissRequest={expanded=false}){Scenario.entries.forEach {scenario->DropdownMenuItem(text={Text(if(scenario==Scenario.BENIGN) "Normal Traffic / Benign" else scenario.title)},onClick={selected=scenario;expanded=false})}}}}
    Badge("Controlled testing");Caption("Submits sample telemetry to the backend. This does not scan your phone or generate a real attack.")
    Primary("Start Scan",!s.busy){start(selected)}
}
@Composable internal fun ScanningScreen(s:ClientState,stop:()->Unit,navigate:(String)->Unit) {
    var progress by remember {mutableFloatStateOf(.02f)}
    LaunchedEffect(s.scanning) {if(s.scanning) {while(true){delay(350);progress=(progress+(.92f-progress)*.07f).coerceAtMost(.92f)}} else navigate(if(s.result!=null) "Scan Results" else "Scan")}
    BackHandler {stop()}
    Spacer(Modifier.height(40.dp));Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(16.dp)){Heading("Scanning...");Caption("Please wait while we analyze\nyour submitted test telemetry.");Spacer(Modifier.height(24.dp));Box(Modifier.size(230.dp),contentAlignment=Alignment.Center){CircularProgressIndicator(progress={progress},modifier=Modifier.fillMaxSize(),color=Violet,trackColor=com.example.ai_ctdrs.theme.Panel,strokeWidth=14.dp);Text("${(progress*100).toInt()}%",style=MaterialTheme.typography.displaySmall,fontWeight=FontWeight.SemiBold)};Spacer(Modifier.height(24.dp));Caption("Awaiting classification and SHAP insights…");Badge("Estimated progress • result from server")}
    OutlinedButton(onClick=stop,modifier=Modifier.fillMaxWidth().height(56.dp),shape=RoundedCornerShape(14.dp)){Text("Stop Scan")}
    Caption("Stopping ends the wait on this device. Submitted analysis may still complete on the server.")
}
@Composable internal fun ResultScreen(s:ClientState,navigate:(String)->Unit,vm:ClientViewModel) {
    val event=s.result
    if(event==null){EmptyState("No scan result","Start a controlled scan to receive a real analysis.");Primary("Go to Scan"){navigate("Scan")};return}
    val a=event.analysis
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically){Symbol(Icons.Outlined.CheckCircle,Green);Column {Text("Scan completed");Caption(event.timestamp)}}
    Badge(if(event.is_demo) "Controlled test result" else "Recorded event")
    if(a==null){EmptyState("Analysis unavailable","The event was returned without an analysis. Refresh activity before submitting another test.");return}
    val incident=s.incidents.find {it.analysis_id==a.id};val color=if(a.classification=="Benign") Green else Red
    Panel {Row(horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically){Symbol(Icons.Outlined.Shield,color,56);Column(Modifier.weight(1f)){Heading(a.classification);Caption(if(a.classification=="Benign") "No threat classified in this profile" else "Threat classified in submitted telemetry")}};incident?.let{Badge(it.severity,severityColor(it.severity))}}
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)){Panel(Modifier.weight(1f)){Text("${"%.1f".format(a.confidence*100)}%",style=MaterialTheme.typography.headlineSmall);Caption("Model confidence")};Panel(Modifier.weight(1f)){Text("${"%.1f".format(a.threat_score)}",style=MaterialTheme.typography.headlineSmall);Caption("Threat score / 100")}}
    Explanation(a)
    Primary("View Details"){navigate("Threat Details")};OutlinedButton(onClick={navigate("Scan")},modifier=Modifier.fillMaxWidth()){Text("Rescan")}
}
@Composable internal fun ThreatDetails(s:ClientState,navigate:(String)->Unit,vm:ClientViewModel) {
    val event=s.result;val a=event?.analysis
    if(a==null){EmptyState("No detection selected","Run an analysis to view its details.");return}
    val incident=s.incidents.find {it.analysis_id==a.id}
    Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)){Symbol(Icons.Outlined.Security,if(a.classification=="Benign") Green else Red,72);Heading(a.classification);Badge(if(event.is_demo) "Controlled test telemetry" else "Recorded event");incident?.let {Badge(it.severity,severityColor(it.severity))}}
    Panel {Text("Detection information",style=MaterialTheme.typography.titleMedium);Caption("Category: ${a.attack_type.ifBlank {a.classification}}");Caption("Detected: ${a.timestamp.ifBlank {event.timestamp}}");Caption("Source: ${event.source_ip}\nDestination: ${event.destination_ip}\nProtocol: ${event.protocol}");Caption("Confidence: ${"%.1f".format(a.confidence*100)}%\nThreat score: ${"%.2f".format(a.threat_score)} / 100")}
    Explanation(a)
    if(incident!=null){Responses(incident.responses);Primary("View incident & response",!s.busy){vm.openIncident(incident.id);navigate("Incident Detail")}}
    else {Caption(if(a.classification=="Benign") "No incident is created for benign traffic. The API does not expose the Allow response record." else "No matching incident is loaded yet. Refresh to retrieve the backend response.");Primary("Refresh response data",!s.busy,vm::refresh)}
}
@Composable internal fun Explanation(a:Analysis) {
    var expanded by rememberSaveable(a.id) {mutableStateOf(true)}
    val features=remember(a.explanation_json){runCatching {Gson().fromJson(a.explanation_json,Array<Feature>::class.java).filter {it.value.isFinite() && it.contribution.isFinite()}.sortedByDescending {abs(it.contribution)}}.getOrDefault(emptyList())}
    Panel {TextButton(onClick={expanded=!expanded},contentPadding=PaddingValues(0.dp),modifier=Modifier.fillMaxWidth()){Icon(Icons.Outlined.AutoAwesome,null);Spacer(Modifier.width(8.dp));Text("Why this result?",modifier=Modifier.weight(1f));Icon(if(expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,if(expanded) "Collapse SHAP" else "Expand SHAP")}
        Caption("SHAP • actual model feature contributions")
        if(expanded){if(features.isEmpty()) Caption("The server did not return a readable SHAP explanation.")
            val maximum=features.maxOfOrNull {abs(it.contribution)}?.coerceAtLeast(.000001) ?: 1.0
            features.forEach {feature->Column(verticalArrangement=Arrangement.spacedBy(6.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(feature.feature.replace('_',' '),modifier=Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium);Text("%+.4f".format(feature.contribution),color=if(feature.contribution>=0) Violet else Blue,style=MaterialTheme.typography.labelMedium)};Row(Modifier.fillMaxWidth().height(6.dp)){Box(Modifier.weight(1f).fillMaxHeight(),contentAlignment=Alignment.CenterEnd){if(feature.contribution<0) Box(Modifier.fillMaxWidth((abs(feature.contribution)/maximum).toFloat()).fillMaxHeight().background(Blue,RoundedCornerShape(3.dp)))};Box(Modifier.width(1.dp).fillMaxHeight().background(Muted));Box(Modifier.weight(1f).fillMaxHeight()){if(feature.contribution>=0) Box(Modifier.fillMaxWidth((abs(feature.contribution)/maximum).toFloat()).fillMaxHeight().background(Violet,RoundedCornerShape(3.dp)))}};Caption("Feature value: ${feature.value}")}}
            Caption("Blue: negative contribution • Violet: positive contribution. Values are supplied by the model explanation.")
        }
    }
}
