package com.example.texttospeech

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

// ---------- Paleta de colores ----------
private val Morado = Color(0xFF6A4C93)
private val MoradoClaro = Color(0xFFB39DDB)
private val Celeste = Color(0xFF4FC3F7)
private val CelesteClaro = Color(0xFFE1F5FE)
private val Blanco = Color(0xFFFFFFFF)

private val EsquemaColores = lightColorScheme(
    primary = Morado,
    onPrimary = Blanco,
    secondary = Celeste,
    onSecondary = Blanco,
    background = CelesteClaro,
    onBackground = Morado,
    surface = Blanco,
    onSurface = Morado,
    primaryContainer = MoradoClaro,
    onPrimaryContainer = Blanco
)

// ---------- Activity ----------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = EsquemaColores) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TextToSpeechScreen()
                }
            }
        }
    }
}

// ---------- Motor de voz ----------
@Composable
fun rememberTextToSpeech(onVoicesReady: (List<Voice>) -> Unit): TextToSpeech? {
    val context = LocalContext.current

    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.let { engine ->
                    engine.language = Locale.US
                    val voces = engine.voices
                        ?.filter { !it.isNetworkConnectionRequired && it.name.isNotBlank() }
                        ?.sortedBy { it.name }
                        ?: emptyList()
                    onVoicesReady(voces)
                }
            }
        }
        ttsInstance
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    return tts
}

// ---------- Pantalla principal ----------
@Composable
fun TextToSpeechScreen() {
    var textoAHablar by remember { mutableStateOf("Hola, bienvenido a Jetpack Compose.") }
    var vocesDisponibles by remember { mutableStateOf<List<Voice>>(emptyList()) }
    var vozSeleccionada by remember { mutableStateOf<Voice?>(null) }

    val tts = rememberTextToSpeech { voces ->
        vocesDisponibles = voces
        if (vozSeleccionada == null && voces.isNotEmpty()) {
            vozSeleccionada = voces.first()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Texto a Voz",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = textoAHablar,
            onValueChange = { textoAHablar = it },
            label = { Text("Escribe el texto a convertir en audio") },
            minLines = 5,
            maxLines = 8,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Selecciona una voz:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            items(vocesDisponibles) { voz ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = voz == vozSeleccionada,
                        onClick = { vozSeleccionada = voz },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                    Text(
                        text = voz.name,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                vozSeleccionada?.let { tts?.voice = it }
                tts?.speak(textoAHablar, TextToSpeech.QUEUE_FLUSH, null, null)
            },
            enabled = tts != null && textoAHablar.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hablar")
        }
    }
}