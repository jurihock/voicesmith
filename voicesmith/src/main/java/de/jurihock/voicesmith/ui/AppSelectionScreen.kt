package de.jurihock.voicesmith.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

@Composable
fun AppSelectionScreen(
    onAppSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val apps = remember {
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 } // Filter out system apps
    }

    LazyColumn {
        items(apps) { app ->
            val appName = packageManager.getApplicationLabel(app).toString()
            val appIcon = packageManager.getApplicationIcon(app)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAppSelected(app.packageName) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = appIcon.toBitmap().asImageBitmap(),
                    contentDescription = appName,
                    modifier = Modifier.size(48.dp)
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = appName)
                }
            }
        }
    }
}
