package com.example.test_1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.test_1.ui.theme.Test1Theme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ricezione dei valori correnti inviati da MainActivity
        val initialDarkTheme = intent.getBooleanExtra("EXTRA_DARK_THEME", false)
        val initialTotalSeconds = intent.getIntExtra("EXTRA_TIMER_SECONDS", 1500)

        setContent {
            var isDarkTheme by remember { mutableStateOf(initialDarkTheme) }

            Test1Theme(darkTheme = isDarkTheme) {
                SecondScreen(
                    initialTotalSeconds = initialTotalSeconds,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = it },
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen(
    initialTotalSeconds: Int,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onBackPressed: () -> Unit
) {
    val activity = (LocalContext.current as? Activity)

    // Scomposizione dei secondi totali in minuti e secondi iniziali
    val initialMinutes = initialTotalSeconds / 60
    val initialSeconds = initialTotalSeconds % 60

    var inputMinutes by remember { mutableStateOf(initialMinutes.toString()) }
    var inputSeconds by remember { mutableStateOf(initialSeconds.toString()) }

    fun calculateTotalSeconds(): Int {
        val min = inputMinutes.toIntOrNull() ?: 0
        val sec = inputSeconds.toIntOrNull() ?: 0
        return (min * 60) + sec
    }

    // Conferma le modifiche e restituisce RESULT_OK
    fun saveAndFinish() {
        val totalSeconds = calculateTotalSeconds()
        val resultIntent = Intent().apply {
            putExtra("EXTRA_TIMER_SECONDS", totalSeconds)
            putExtra("EXTRA_DARK_THEME", isDarkTheme)
        }
        activity?.setResult(Activity.RESULT_OK, resultIntent)
        activity?.finish()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impostazioni Timer") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Annulla l'operazione SENZA salvare le modifiche
                        activity?.setResult(Activity.RESULT_CANCELED)
                        onBackPressed()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro"
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Switch per il Tema Scuro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tema Scuro",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onThemeToggle
                )
            }

            HorizontalDivider()

            // Campi di testo affiancati per Minuti e Secondi
            Text(
                text = "Imposta Durata Timer:",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = inputMinutes,
                    onValueChange = { inputMinutes = it },
                    label = { Text("Minuti") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = inputSeconds,
                    onValueChange = { inputSeconds = it },
                    label = { Text("Secondi") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = { saveAndFinish() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salva e Torna Indietro")
            }
        }
    }
}