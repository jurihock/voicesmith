package de.jurihock.voicesmith.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.roundToInt

@Composable
fun IntParameterScreen(modifier: Modifier = Modifier,
                       name: String, unit: String, value: State<Int>,
                       min: Int, max: Int, inc: Int,
                       onChange: (value: Int) -> Unit) {

  fun get() : String {
    return if (value.value > 0) "+${value.value}"
    else "${value.value}"
  }

  fun set(newValue: Int) {
    if (newValue != value.value) {
      onChange(newValue)
    }
  }

  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "$name ${get()}$unit",
      modifier = Modifier.fillMaxWidth(),
      textAlign = TextAlign.Center
    )
    Slider(
      value = value.value.toFloat(),
      valueRange = min.toFloat()..max.toFloat(),
      steps = (max - min - 1) / inc,
      onValueChange = { set(it.roundToInt()) })
  }

}
