package de.jurihock.voicesmith.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun DeviceSelectorScreen(modifier: Modifier = Modifier,
                         textInput: String, textOutput: String,
                         onSelectInputDevice: () -> Unit,
                         onSelectOutputDevice: () -> Unit) {

  Row(modifier = modifier.fillMaxWidth()) {
    OutlinedButton(modifier = Modifier.weight(1f), onClick = onSelectInputDevice) {
      Text(text = textInput)
    }
    Spacer(modifier = Modifier.width(Dp(UI.PADDING)))
    OutlinedButton(modifier = Modifier.weight(1f), onClick = onSelectOutputDevice) {
      Text(text = textOutput)
    }
  }

}
