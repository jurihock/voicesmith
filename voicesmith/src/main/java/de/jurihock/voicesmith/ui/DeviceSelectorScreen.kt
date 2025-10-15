package de.jurihock.voicesmith.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun DeviceSelectorScreen(modifier: Modifier = Modifier,
                         textInput: String, textOutput: String,
                         textMono: String, textStereo: String,
                         channels: State<Int>,
                         onSelectInputDevice: () -> Unit,
                         onSelectOutputDevice: () -> Unit,
                         onSelectChannels: () -> Unit) {

  Row(modifier = modifier.fillMaxWidth()) {
    OutlinedButton(modifier = Modifier.weight(1f), onClick = onSelectInputDevice) {
      Text(text = textInput)
    }
    Spacer(modifier = Modifier.width(Dp(UI.PADDING)))
    OutlinedButton(modifier = Modifier.weight(0.5f), onClick = onSelectChannels) {
      Text(text = if (channels.value != 2) textMono else textStereo)
    }
    Spacer(modifier = Modifier.width(Dp(UI.PADDING)))
    OutlinedButton(modifier = Modifier.weight(1f), onClick = onSelectOutputDevice) {
      Text(text = textOutput)
    }
  }

}
