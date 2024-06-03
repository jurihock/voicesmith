package de.jurihock.voicesmith.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MainTheme(dark: Boolean = isSystemInDarkTheme(),
              content: @Composable () -> Unit) {

  val context = LocalContext.current

  val colors = when {
    dark -> dynamicDarkColorScheme(context)
    else -> dynamicLightColorScheme(context)
  }

  MaterialTheme(
    colorScheme = colors,
    content = content
  )

}
