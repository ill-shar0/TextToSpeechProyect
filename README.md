# Texto a Voz (Jetpack Compose)

App Android que convierte texto escrito en audio hablado, con selección de voz.

## Funcionalidad
- Campo de texto multilinea (tipo *textarea*) para ingresar el texto.
- Botón **Hablar** que reproduce el texto con el motor TTS del sistema.
- Lista de voces disponibles (RadioButton, selección única).
- Interfaz en tonos celeste, blanco y morado.

## Lenguaje y stack
- **Kotlin** + **Jetpack Compose** (Material3).
- API nativa de Android: `android.speech.tts.TextToSpeech`.

## Estructura
| Archivo / Función | Responsabilidad |
|---|---|
| `MainActivity` | Punto de entrada, aplica el tema de colores. |
| `rememberTextToSpeech()` | Crea, mantiene y libera el motor TTS; carga la lista de voces filtradas (excluye las que requieren red). |
| `TextToSpeechScreen()` | UI: textarea, lista de voces y botón de reproducción. |

## Requisitos
- Android Studio con AGP/Gradle compatible (ver nota abajo).
- Dispositivo con **Google Text-to-Speech (Speech Services by Google)** instalado y seleccionado como motor predeterminado en *Ajustes > Accesibilidad > Salida de texto a voz*.

## Notas conocidas
- **Voces con nombres ilegibles** (ej. `en-au-x-aua-local`): ocurre cuando el dispositivo usa el motor de respaldo de Android en vez de Google TTS. Solución: cambiar el motor predeterminado en el dispositivo, o forzarlo en código con `TextToSpeech(context, listener, "com.google.android.tts")`.
- **Error de build `getPrivacySandboxSdkInfo()`**: desfase entre la versión de Android Studio y el AGP/Gradle del proyecto. Generalmente se puede ignorar (no bloquea el build) o resolverse actualizando `gradle-wrapper.properties`.

## Pendiente / mejoras posibles
- Forzar motor de Google TTS por defecto para evitar dependencia de configuración del usuario.
- Mostrar `voz.locale.displayName` en vez de `voz.name` para nombres más legibles.
