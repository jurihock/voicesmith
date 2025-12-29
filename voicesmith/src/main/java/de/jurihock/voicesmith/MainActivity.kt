package de.jurihock.voicesmith

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import de.jurihock.voicesmith.etc.Preferences
import de.jurihock.voicesmith.service.AudioService
import de.jurihock.voicesmith.service.AudioServiceActivity
import de.jurihock.voicesmith.service.OverlayService
import de.jurihock.voicesmith.ui.AppSelectionScreen
import de.jurihock.voicesmith.ui.IntParameterScreen
import de.jurihock.voicesmith.ui.MainTheme
import de.jurihock.voicesmith.ui.UI
import kotlinx.coroutines.launch

class MainActivity : AudioServiceActivity() {

    private val preferences by lazy { Preferences(this) }
    private var pendingPackageName: String? = null

    private val overlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Settings.canDrawOverlays(this)) {
            pendingPackageName?.let { launchApp(it) }
            pendingPackageName = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainTheme {
                var showAppSelection by remember { mutableStateOf(false) }

                if (showAppSelection) {
                    AppSelectionScreen(
                        onAppSelected = { packageName ->
                            val currentApps = preferences.clonedApps.toMutableSet()
                            currentApps.add(packageName)
                            preferences.clonedApps = currentApps
                            showAppSelection = false
                        }
                    )
                } else {
                    BlackBoxMainScreen(
                        preferences = preferences,
                        onAddAppClicked = { showAppSelection = true },
                        onAppClicked = { packageName ->
                            if (Settings.canDrawOverlays(this)) {
                                launchApp(packageName)
                            } else {
                                pendingPackageName = packageName
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:$packageName")
                                )
                                overlayPermissionLauncher.launch(intent)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun launchApp(packageName: String) {
        // Start Audio Service
        val audioServiceIntent = Intent(this, AudioService::class.java)
        startService(audioServiceIntent)

        // Start Overlay Service
        val overlayServiceIntent = Intent(this, OverlayService::class.java)
        startService(overlayServiceIntent)

        // Launch the selected app
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let { startActivity(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlackBoxMainScreen(
    preferences: Preferences,
    onAddAppClicked: () -> Unit,
    onAppClicked: (String) -> Unit
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val clonedApps = remember { preferences.clonedApps.toList() }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    val delay = remember { mutableIntStateOf(preferences.delay) }
    val pitch = remember { mutableIntStateOf(preferences.pitch) }
    val timbre = remember { mutableIntStateOf(preferences.timbre) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            Column(modifier = Modifier.padding(Dp(UI.PADDING))) {
                Spacer(modifier = Modifier.weight(1f))
                IntParameterScreen(
                    name = "Delay", unit = "ms", value = delay,
                    min = 0, max = 1000, inc = 50,
                    onChange = {
                        delay.intValue = it
                        preferences.delay = it
                    })
                Spacer(modifier = Modifier.height(Dp(UI.PADDING)))
                IntParameterScreen(
                    name = "Pitch", unit = "st", value = pitch,
                    min = -12, max = +12, inc = 1,
                    onChange = {
                        pitch.intValue = it
                        preferences.pitch = it
                    })
                Spacer(modifier = Modifier.height(Dp(UI.PADDING)))
                IntParameterScreen(
                    name = "Timbre", unit = "st", value = timbre,
                    min = -12, max = +12, inc = 1,
                    onChange = {
                        timbre.intValue = it
                        preferences.timbre = it
                    })
                Spacer(modifier = Modifier.weight(1f))
            }
        },
        sheetPeekHeight = 0.dp
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("BlackBox") },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddAppClicked) {
                    Icon(Icons.Filled.Add, contentDescription = "Add App")
                }
            }
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(clonedApps) { packageName ->
                    try {
                        val appInfo = packageManager.getApplicationInfo(packageName, 0)
                        val appIcon = packageManager.getApplicationIcon(appInfo)
                        val appName = packageManager.getApplicationLabel(appInfo).toString()

                        AppIcon(
                            icon = appIcon,
                            name = appName,
                            onClick = { onAppClicked(packageName) }
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        // App might have been uninstalled
                    }
                }
            }
        }
    }
}

@Composable
fun AppIcon(
    icon: Drawable,
    name: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp).clickable(onClick = onClick)
    ) {
        Image(
            bitmap = icon.toBitmap().asImageBitmap(),
            contentDescription = name,
            modifier = Modifier.size(64.dp)
        )
        Text(text = name, modifier = Modifier.padding(top = 4.dp))
    }
}
