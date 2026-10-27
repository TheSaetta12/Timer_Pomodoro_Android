package com.example.test_1

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test_1.ui.theme.Test1Theme
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {

    private var initialTimeInSeconds by mutableIntStateOf(1500)
    private var isDarkTheme by mutableStateOf(false)

    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedTimeSeconds = result.data?.getIntExtra("EXTRA_TIMER_SECONDS", initialTimeInSeconds) ?: initialTimeInSeconds
            val updatedDarkTheme = result.data?.getBooleanExtra("EXTRA_DARK_THEME", isDarkTheme) ?: isDarkTheme
            
            initialTimeInSeconds = updatedTimeSeconds
            isDarkTheme = updatedDarkTheme
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Test1Theme(darkTheme = isDarkTheme) {
                var showSplashScreen by remember { mutableStateOf(true) }

                if (showSplashScreen) {
                    SplashScreen(onTimeout = { showSplashScreen = false })
                } else {
                    MainScreen(
                        initialTimeSeconds = initialTimeInSeconds,
                        onOpenSettings = {
                            val intent = Intent(this, SecondActivity::class.java).apply {
                                putExtra("EXTRA_DARK_THEME", isDarkTheme)
                                putExtra("EXTRA_TIMER_SECONDS", initialTimeInSeconds)
                            }
                            settingsLauncher.launch(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Schermata di avvio mostrata per 2 secondi
    LaunchedEffect(Unit) {
        delay(2000L.milliseconds)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.tomato),
                contentDescription = "Logo Pomodoro",
                modifier = Modifier.size(160.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Timer Pomodoro by Pietro",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    initialTimeSeconds: Int,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    var timeRemaining by remember(initialTimeSeconds) { mutableIntStateOf(initialTimeSeconds) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isRunning, key2 = timeRemaining) {
        if (isRunning && timeRemaining > 0) {
            delay(1000L.milliseconds)
            timeRemaining--
        } else if (isRunning && timeRemaining == 0) {
            isRunning = false
            Toast.makeText(context, "Tempo scaduto!", Toast.LENGTH_LONG).show()

            try {
                val mediaPlayer = MediaPlayer.create(context, R.raw.sound_finished)
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener { mp -> mp.release() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timer Pomodoro by Pietro") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Impostazioni"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.tomato),
                contentDescription = "Pomodoro",
                modifier = Modifier
                    .size(180.dp)
                    .padding(bottom = 24.dp)
            )

            val minutes = timeRemaining / 60
            val seconds = timeRemaining % 60
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

            Text(
                text = formattedTime,
                fontSize = 48.sp,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { isRunning = true },
                    enabled = !isRunning && timeRemaining > 0
                ) {
                    Text("Avvia")
                }

                Button(
                    onClick = { isRunning = false },
                    enabled = isRunning
                ) {
                    Text("Pausa")
                }

                Button(
                    onClick = {
                        isRunning = false
                        timeRemaining = initialTimeSeconds
                    }
                ) {
                    Text("Reset")
                }
            }
        }
    }
}
