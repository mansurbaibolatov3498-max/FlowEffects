package com.floweffects.editor.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BgDeepBlack = Color(0xFF0A0B0E)
val PanelDark = Color(0xFF14161D)
val PanelElevated = Color(0xFF1E212B)
val AccentCyan = Color(0xFF00E5FF)
val AccentPurple = Color(0xFF7C4DFF)
val KeyframeGold = Color(0xFFFFD54F)
val TextPrimary = Color(0xFFF3F4F6)
val TextSecondary = Color(0xFF8E95A5)

data class LayerItem(
    val id: Int,
    val name: String,
    val type: String,
    val color: Color,
    val keyframes: List<Float>
)

@Composable
fun FlowEffectsEditorScreen() {
    var currentTimeMs by remember { mutableStateOf(1420L) }
    var isPlaying by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf("Timeline") }

    val layers = remember {
        listOf(
            LayerItem(1, "Adjustment Layer 1 [RSMB + Glow]", "ADJUSTMENT", AccentPurple, listOf(0.1f, 0.45f, 0.85f)),
            LayerItem(2, "Null 1 [Camera Shake]", "NULL", Color(0xFFFF5252), listOf(0.2f, 0.35f, 0.5f, 0.7f)),
            LayerItem(3, "Main_Clip_4K_120fps.mp4", "VIDEO", AccentCyan, listOf(0.05f, 0.6f, 0.95f))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepBlack)
            .statusBarsPadding()
    ) {
        TopStudioBar()
        CompositionViewport(modifier = Modifier.fillMaxWidth().weight(0.42f))
        PlaybackControlBar(currentTimeMs = currentTimeMs, isPlaying = isPlaying, onPlayPause = { isPlaying = !isPlaying })
        Box(modifier = Modifier.fillMaxWidth().weight(0.48f).background(PanelDark)) {
            when (activeTab) {
                "Timeline" -> TimelineSection(layers = layers, currentTimeMs = currentTimeMs) { currentTimeMs = it }
                "Graph" -> GraphEditorSection()
                "Effects" -> EffectsStackSection()
            }
        }
        BottomToolDock(activeTab = activeTab, onSelectTab = { activeTab = it })
    }
}

@Composable
fun TopStudioBar() {
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp).background(PanelDark).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp))
                    .background(Brush.linearGradient(listOf(AccentCyan, AccentPurple))),
                contentAlignment = Alignment.Center
            ) {
                Text("Fe", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Flow Effects", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Comp 1 • 1080x1920 • 60 FPS", color = TextSecondary, fontSize = 10.sp)
            }
        }
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.height(30.dp)
        ) {
            Text("EXPORT", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CompositionViewport(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(12.dp).clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF050507)).border(1.dp, PanelElevated, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            drawLine(Color.White.copy(alpha = 0.08f), Offset(w / 2, 0f), Offset(w / 2, h), 1f)
            drawLine(Color.White.copy(alpha = 0.08f), Offset(0f, h / 2), Offset(w, h / 2), 1f)
        }
        Text("PREVIEW CANVAS", color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun PlaybackControlBar(currentTimeMs: Long, isPlaying: Boolean, onPlayPause: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(40.dp).background(PanelElevated).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = String.format("00:00:%02d:%02d", (currentTimeMs / 1000) % 60, (currentTimeMs % 1000) / 16),
            color = AccentCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = TextPrimary)
            }
            IconButton(onClick = onPlayPause, modifier = Modifier.size(36.dp)) {
                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = AccentCyan)
            }
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.SkipNext, contentDescription = null, tint = TextPrimary)
            }
        }
        Text(
            text = "◆ +KEY", color = KeyframeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color.Black.copy(alpha = 0.4f))
                .clickable {}.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TimelineSection(layers: List<LayerItem>, currentTimeMs: Long, onScrub: (Long) -> Unit) {
    var playheadFraction by remember { mutableStateOf(0.35f) }
    Box(
        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectHorizontalDragGestures { _, dragAmount ->
                playheadFraction = (playheadFraction + dragAmount / size.width).coerceIn(0f, 1f)
                onScrub((playheadFraction * 5000).toLong())
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 24.dp, start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(layers) { layer -> LayerTrackRow(layer = layer) }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val xPos = size.width * playheadFraction
            drawLine(color = AccentCyan, start = Offset(xPos, 0f), end = Offset(xPos, size.height), strokeWidth = 2.dp.toPx())
            drawCircle(color = AccentCyan, radius = 5.dp.toPx(), center = Offset(xPos, 8.dp.toPx()))
        }
    }
}

@Composable
fun LayerTrackRow(layer: LayerItem) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(PanelElevated).padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(layer.color))
            Spacer(modifier = Modifier.width(6.dp))
            Text(layer.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(
            modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(4.dp))
                .background(layer.color.copy(alpha = 0.2f))
        ) {
            layer.keyframes.forEach { fraction ->
                val cx = size.width * fraction; val cy = size.height / 2; val r = 5.dp.toPx()
                val diamond = Path().apply {
                    moveTo(cx, cy - r); lineTo(cx + r, cy); lineTo(cx, cy + r); lineTo(cx - r, cy); close()
                }
                drawPath(diamond, color = KeyframeGold)
            }
        }
    }
}

@Composable
fun GraphEditorSection() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SPEED & VALUE GRAPH EDITOR", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Canvas(
            modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(8.dp))
                .background(BgDeepBlack).border(1.dp, PanelElevated, RoundedCornerShape(8.dp))
        ) {
            val path = Path().apply {
                moveTo(20f, size.height - 20f)
                cubicTo(size.width * 0.45f, size.height - 20f, size.width * 0.55f, 20f, size.width - 20f, 20f)
            }
            drawPath(path, color = AccentPurple, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()))
        }
    }
}

@Composable
fun EffectsStackSection() {
    val activeEffects = listOf(
        "S_Shake (Amplitude: 45, Freq: 8.0)",
        "RSMB (Blur Amount: 0.65)",
        "Deep Glow (Radius: 120)",
        "Unsharp Mask (Sharpen: 1.4)"
    )
    LazyColumn(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(activeEffects) { effect ->
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(PanelElevated).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("fx  $effect", color = TextPrimary, fontSize = 12.sp)
                Text("ON", color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BottomToolDock(activeTab: String, onSelectTab: (String) -> Unit) {
    val tabs = listOf("Timeline", "Graph", "Effects")
    Row(
        modifier = Modifier.fillMaxWidth().height(50.dp).background(BgDeepBlack)
            .border(1.dp, PanelElevated).navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { tab ->
            val selected = tab == activeTab
            Text(
                text = tab.uppercase(),
                color = if (selected) AccentCyan else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onSelectTab(tab) }.padding(8.dp)
            )
        }
    }
}
