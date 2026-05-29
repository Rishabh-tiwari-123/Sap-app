package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val CyberColorScheme = darkColorScheme(
  primary = CyberPrimary,
  secondary = CyberSecondary,
  tertiary = CyberTertiary,
  background = CyberVoid,
  surface = CyberCabinet,
  onPrimary = CyberVoid,
  onSecondary = TextPrimary,
  onTertiary = CyberVoid,
  onBackground = TextPrimary,
  onSurface = TextPrimary,
  outline = CyberOutline,
  surfaceVariant = CyberPanel,
  onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force futuristic deep-dark mode as default to fit the prompt
  dynamicColor: Boolean = false, // Disable dynamic colors to enforce the custom retro-arcade cyan scheme
  content: @Composable () -> Unit,
) {
  val colorScheme = CyberColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

