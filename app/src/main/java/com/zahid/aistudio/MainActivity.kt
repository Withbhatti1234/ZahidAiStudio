package com.zahid.aistudio

import android.net.Uri
import android.os.Bundle
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zahid.aistudio.ai.BitmapUtils
import com.zahid.aistudio.ai.LocalEnhancer
import com.zahid.aistudio.ai.MaskProcessor
import com.zahid.aistudio.export.ImageExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZahidAIStudioApp() }
    }
}

@Composable
fun ZahidAIStudioApp() {
    var uri by remember { mutableStateOf<Uri?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri = it }
    if (uri == null) HomeScreen { picker.launch("image/*") }
    else EditorScreen(uri!!, onBack = { uri = null }, onPick = { picker.launch("image/*") })
}

@Composable
private fun HomeScreen(onPick: () -> Unit) {
    Scaffold { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(20.dp)) {
            Text("Zahid AI Studio", style = MaterialTheme.typography.headlineLarge)
            Text("AI Photo & Video Editor", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(18.dp))
            Button(onClick = onPick, Modifier.fillMaxWidth().height(56.dp)) {
                Text("＋ Gallery سے تصویر منتخب کریں")
            }
            Spacer(Modifier.height(18.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(listOf("✨ AI Enhance","🧹 Object Eraser","🎨 Background","🙂 Face Enhance","🌙 Night Fix","🔍 4K Upscale")) {
                    Card(Modifier.height(105.dp)) { Box(Modifier.fillMaxSize(), Alignment.Center) { Text(it) } }
                }
            }
        }
    }
}

@Composable
private fun EditorScreen(uri: Uri, onBack: () -> Unit, onPick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var original by remember { mutableStateOf<Bitmap?>(null) }
    var result by remember { mutableStateOf<Bitmap?>(null) }
    var mode by remember { mutableStateOf("Original") }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(uri) {
        original = withContext(Dispatchers.IO) { BitmapUtils.load(context, uri) }
        result = null; mode = "Original"; message = ""
    }

    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Back") }
            Text("AI Editor", color = Color.White, style = MaterialTheme.typography.titleLarge)
            TextButton(enabled = result != null && !busy, onClick = {
                result?.let { ok -> if (ImageExporter.saveToGallery(context, ok, "ZahidAI_${System.currentTimeMillis()}.png")) message = "تصویر Gallery میں save ہو گئی" else message = "Save failed" }
            }) { Text("Save") }
        }
        Box(Modifier.weight(1f).fillMaxWidth(), Alignment.Center) {
            val bmp = if (mode == "Original") original else result ?: original
            if (bmp != null) Image(bmp.asImageBitmap(), "preview", Modifier.fillMaxWidth(), ContentScale.Fit)
            else AsyncImage(uri, "preview", Modifier.fillMaxWidth(), contentScale = ContentScale.Fit)
        }
        Text(if (busy) "AI processing…" else message.ifEmpty { mode }, color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(6.dp))

        Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.spacedBy(6.dp)) {
            Button(enabled = !busy && original != null, onClick = {
                scope.launch {
                    busy = true
                    result = withContext(Dispatchers.Default) { LocalEnhancer.enhance(original!!) }
                    mode = "Enhanced"; busy = false
                }
            }, Modifier.weight(1f)) { Text("✨ Enhance") }

            OutlinedButton(enabled = !busy && original != null, onClick = {
                scope.launch {
                    busy = true
                    result = withContext(Dispatchers.Default) {
                        MaskProcessor.eraseCircle(original!!, original!!.width*.5f, original!!.height*.5f,
                            minOf(original!!.width, original!!.height)*.10f)
                    }
                    mode = "Eraser"; busy = false
                    message = "Demo erase applied — production inpainting model next"
                }
            }, Modifier.weight(1f)) { Text("🧹 Eraser") }

            OutlinedButton(enabled = !busy && original != null, onClick = {
                scope.launch {
                    busy = true
                    result = withContext(Dispatchers.Default) { MaskProcessor.removeBackgroundPreview(original!!) }
                    mode = "Background"; busy = false
                    message = "Background preview applied"
                }
            }, Modifier.weight(1f)) { Text("🎨 BG") }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), Arrangement.spacedBy(6.dp)) {
            OutlinedButton(enabled = result != null && !busy, onClick = { mode = "Original" }, Modifier.weight(1f)) { Text("Original") }
            OutlinedButton(enabled = result != null && !busy, onClick = { mode = "Result" }, Modifier.weight(1f)) { Text("Before / After") }
            OutlinedButton(onClick = onPick, Modifier.weight(1f)) { Text("Change") }
        }
        Text("MVP: local enhancement + preview tools. Generative AI models are not bundled.",
            color = Color.LightGray, modifier = Modifier.padding(8.dp))
    }
}
