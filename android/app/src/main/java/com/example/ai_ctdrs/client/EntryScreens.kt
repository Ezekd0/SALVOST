package com.example.ai_ctdrs.client

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.unit.dp
import com.example.ai_ctdrs.theme.*

@Composable internal fun Onboarding(page: Int, next: () -> Unit, finish: () -> Unit) {
    val titles = listOf("Detect Threats\nBefore They Strike", "Respond Faster\nwith AI", "Monitor Everything\nin Real Time")
    val descriptions = listOf("AI-powered detection and explainable insights to understand suspicious traffic.", "Automated insights and response records help you investigate with confidence.", "See your security events, incidents and backend status in one place.")
    Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement=Arrangement.spacedBy(20.dp)) {
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End) { TextButton(onClick=finish) { Text("Skip",color=Muted) } }
        Crossfade(page,label="Onboarding artwork") { SecurityArt(Modifier.fillMaxWidth().heightIn(min=220.dp,max=320.dp).aspectRatio(1f), listOf("shield","servers","monitor")[page]) }
        Heading(titles[page]); Caption(descriptions[page]); Spacer(Modifier.height(36.dp))
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
            Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) { repeat(3) { Surface(shape=CircleShape,color=if(it==page) Violet else Color(0xFF303C55),modifier=Modifier.size(8.dp)) {} } }
            if(page<2) FilledIconButton(onClick=next,shape=CircleShape,modifier=Modifier.size(60.dp)) { Icon(Icons.AutoMirrored.Outlined.ArrowForward,"Next") }
        }
        if(page==2) Primary("Get Started",click=finish)
    }
}
@Composable internal fun AuthScreen(s: ClientState, vm: ClientViewModel) {
    var register by rememberSaveable { mutableStateOf(false) }; var server by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }; var name by rememberSaveable { mutableStateOf("") }; var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }; var confirm by remember { mutableStateOf("") }
    var validation by remember { mutableStateOf<String?>(null) }
    BackHandler(register || server) { if(server) server=false else register=false }
    Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Spacer(Modifier.height(16.dp)); Brand(large=true); Spacer(Modifier.height(16.dp))
        if(s.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        s.error?.let { Text(it,color=Red) }; validation?.let { Text(it,color=Red) }
        when {
            server -> { Heading("Server connection"); ServerScreen(s,vm); TextButton(onClick={server=false}) { Text("Back to sign in") } }
            else -> {
                Heading(if(register) "Create Account" else "Welcome Back"); Caption(if(register) "Create your protection account. You will be signed in when registration succeeds." else "Sign in to continue to your protection dashboard.")
                if(register) { Field("Full name",name,icon=Icons.Outlined.Person) {name=it}; Field("Email",email,icon=Icons.Outlined.Email) {email=it} }
                Field("Username",username) {username=it}; Field("Password",password,secret=true,icon=Icons.Outlined.Lock) {password=it}
                if(register) Field("Confirm password",confirm,secret=true,icon=Icons.Outlined.Lock) {confirm=it}
                Spacer(Modifier.height(6.dp))
                Primary(if(register) "Continue" else "Sign In",!s.busy) {
                    validation = when { username.isBlank() || password.isBlank() -> "Enter your username and password."; register && (name.isBlank() || email.isBlank()) -> "Enter your full name and email."; register && password != confirm -> "Passwords do not match."; else -> null }
                    if(validation==null) { if(register) vm.authenticate(username,password,Registration(name.trim(),email.trim(),username.trim(),password,confirm)) else vm.authenticate(username,password,null) }
                }
                TextButton(onClick={register=!register; validation=null},enabled=!s.busy,modifier=Modifier.align(Alignment.CenterHorizontally)) { Text(if(register) "Already have an account? Sign in" else "Don't have an account? Sign up") }
                TextButton(onClick={server=true},modifier=Modifier.align(Alignment.CenterHorizontally)) { Icon(Icons.Outlined.Dns,null,modifier=Modifier.size(16.dp)); Spacer(Modifier.width(8.dp)); Text("Server connection",color=Muted) }
            }
        }
    }
}
@Composable internal fun ServerScreen(s: ClientState, vm: ClientViewModel) {
    var url by remember(s.url) { mutableStateOf(s.url) }
    Field("Backend URL",url,icon=Icons.Outlined.Dns) {url=it}; Caption("Production: https://salvost.onrender.com/\nLocal and insecure development URLs are blocked in the production app.")
    Primary("Save & check server",!s.busy) {vm.saveServer(url)}; Caption(s.health); Caption("Changing servers signs you out.")
}
