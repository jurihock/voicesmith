package de.jurihock.voicesmith.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun BigToggleButtonScreen(modifier: Modifier = Modifier,
                          textOn: String, textOff: String, value: State<Boolean>,
                          onToggle: () -> Unit) {

  Button(modifier = modifier.fillMaxWidth(), onClick = onToggle) {
    Text(
      text = if (value.value) textOff else textOn,
      modifier = Modifier.padding(Dp(0f), Dp(UI.PADDING * 2f)))
  }

}
