package com.example.ai_ctdrs.client

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.ai_ctdrs.theme.*
import kotlin.math.*

@Composable internal fun Heading(text: String) { Text(text, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold) }
@Composable internal fun Caption(text: String) { Text(text, color = Muted, style = MaterialTheme.typography.bodyMedium) }
@Composable internal fun Panel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = com.example.ai_ctdrs.theme.Panel), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF172333))) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
    }
}
@Composable internal fun Primary(text: String, enabled: Boolean = true, click: () -> Unit) {
    Button(onClick = click, enabled = enabled, shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), contentPadding = PaddingValues(), modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clip(RoundedCornerShape(14.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF5433FF), Violet)))) {
        Text(text, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.SemiBold)
    }
}
@Composable internal fun Badge(text: String, color: Color = Violet) { Text(text, color = color, style = MaterialTheme.typography.labelMedium, modifier = Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = .15f)).padding(horizontal = 10.dp, vertical = 5.dp)) }
internal fun severityColor(value: String) = when(value.lowercase()) { "critical", "high" -> Red; "moderate", "medium" -> Amber; "low", "contained", "resolved" -> Green; else -> Blue }
@Composable internal fun Symbol(icon: ImageVector, color: Color = Violet, size: Int = 44) {
    Box(Modifier.size(size.dp).clip(RoundedCornerShape(14.dp)).background(color.copy(alpha = .16f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size((size * .53f).dp)) }
}
@Composable internal fun Field(label: String, value: String, secret: Boolean = false, icon: ImageVector = Icons.Outlined.Person, change: (String) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(value, change, label = { Text(label) }, leadingIcon = { Icon(icon, null) },
        trailingIcon = if(secret) ({ IconButton(onClick = { visible = !visible }) { Icon(if(visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, if(visible) "Hide password" else "Show password") } }) else null,
        visualTransformation = if(secret && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = if(secret) KeyboardType.Password else if(label == "Email") KeyboardType.Email else KeyboardType.Text),
        singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = com.example.ai_ctdrs.theme.Panel, focusedContainerColor = com.example.ai_ctdrs.theme.Panel, unfocusedBorderColor = Color(0xFF1A2535)), modifier = Modifier.fillMaxWidth())
}
@Composable internal fun Brand(large: Boolean = false) {
    if(large) Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) { SecurityArt(Modifier.size(88.dp), "logo"); Text("AI-DRIVEN ANDROID RUNTIME THREAT DETECTION AND AUTONOMOUS RESPONSE SYSTEM", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
    else Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { SecurityArt(Modifier.size(34.dp), "logo"); Text("AI-DRIVEN ANDROID RUNTIME THREAT DETECTION AND AUTONOMOUS RESPONSE SYSTEM", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
}
// Vector artwork scales with the phone, with no screenshot text or device chrome baked in.
@Composable internal fun SecurityArt(modifier: Modifier = Modifier, kind: String = "shield") {
    Canvas(modifier) {
        val w = size.width; val h = size.height; val c = center; val r = min(w,h)
        drawCircle(Brush.radialGradient(listOf(Violet.copy(alpha=.25f), Color.Transparent), c, r*.5f), r*.5f)
        if(kind == "radar") {
            for(n in 1..4) drawCircle(Blue.copy(alpha = .12f + n*.04f), r*n/10, style = Stroke(if(n==4) r*.045f else 1f))
            for(n in 0..7) { val a = n*PI/4; drawLine(Blue.copy(alpha=.12f), c, Offset(c.x+cos(a).toFloat()*r*.4f,c.y+sin(a).toFloat()*r*.4f)) }
            drawArc(Brush.sweepGradient(listOf(Blue,Violet,Blue)), -90f, 300f, false, topLeft = Offset(c.x-r*.4f,c.y-r*.4f), size = androidx.compose.ui.geometry.Size(r*.8f,r*.8f), style = Stroke(r*.055f, cap = StrokeCap.Round))
        }
        if(kind != "logo" && kind != "radar") {
            val base = Path().apply { moveTo(w*.16f,h*.68f); lineTo(w*.5f,h*.85f); lineTo(w*.85f,h*.67f); lineTo(w*.5f,h*.52f); close() }
            drawPath(base, Brush.linearGradient(listOf(Violet,Color(0xFF241278)))); drawPath(base, Blue.copy(alpha=.5f), style=Stroke(2f))
            for(n in 0..12) drawCircle(Violet.copy(alpha=.3f), 2f, Offset(w*(.1f+n*.065f), h*(.35f + sin(n*2f)*.12f)))
        }
        if(kind == "servers") {
            for(n in 2 downTo 0) { val y=h*.25f+n*h*.13f; drawRoundRect(Brush.linearGradient(listOf(Violet,Color(0xFF20144F))), Offset(w*.3f,y), androidx.compose.ui.geometry.Size(w*.4f,h*.16f), androidx.compose.ui.geometry.CornerRadius(12f)); drawCircle(Blue,r*.018f,Offset(w*.62f,y+h*.08f)); drawLine(Color(0xFF9D81FF),Offset(w*.35f,y+h*.065f),Offset(w*.5f,y+h*.065f),3f) }
        } else if(kind == "monitor") {
            drawRoundRect(Brush.linearGradient(listOf(Violet,Color(0xFF13152C))),Offset(w*.18f,h*.2f),androidx.compose.ui.geometry.Size(w*.64f,h*.4f),androidx.compose.ui.geometry.CornerRadius(12f),style=Stroke(5f))
            for(n in 0..4) drawRect(Blue.copy(alpha=.4f+n*.1f),Offset(w*(.25f+n*.1f),h*(.48f-n*.04f)),androidx.compose.ui.geometry.Size(w*.06f,h*(.06f+n*.04f)))
            drawLine(Violet,Offset(w*.5f,h*.61f),Offset(w*.5f,h*.72f),8f)
        } else {
            val scale = if(kind=="logo") .8f else .42f; val sw=r*scale; val x=c.x; val y=c.y-r*.02f
            val shield = Path().apply { moveTo(x,y-sw*.5f); lineTo(x+sw*.4f,y-sw*.28f); lineTo(x+sw*.35f,y+sw*.18f); quadraticTo(x+sw*.3f,y+sw*.38f,x,y+sw*.55f); quadraticTo(x-sw*.3f,y+sw*.38f,x-sw*.35f,y+sw*.18f); lineTo(x-sw*.4f,y-sw*.28f); close() }
            drawPath(shield,Brush.linearGradient(listOf(Color(0xFFAE6AFF),Violet,Blue))); drawPath(shield,Color(0xFFA78FFF),style=Stroke(2f))
            val inner=Path().apply { moveTo(x,y-sw*.3f); lineTo(x+sw*.23f,y-sw*.16f); lineTo(x+sw*.2f,y+sw*.15f); lineTo(x,y+sw*.34f); lineTo(x-sw*.2f,y+sw*.15f); lineTo(x-sw*.23f,y-sw*.16f); close() }; drawPath(inner,Color(0xFF151345)); drawLine(Blue,Offset(x-sw*.1f,y),Offset(x,y+sw*.13f),sw*.06f); drawLine(Blue,Offset(x,y+sw*.13f),Offset(x+sw*.13f,y-sw*.1f),sw*.06f)
        }
    }
}
@Composable internal fun Waves(modifier: Modifier = Modifier) { Canvas(modifier) {
    for(row in 0..14) for(col in 0..65) { val x=size.width*col/65; val y=size.height*.25f+sin(col*.10f+row*.15f)*size.height*.12f+row*size.height*.013f; drawCircle(Violet.copy(alpha=.15f+row*.035f),1.5f,Offset(x,y)); drawCircle(Blue.copy(alpha=.12f+row*.025f),1.4f,Offset(x,size.height-y*.7f)) }
} }
