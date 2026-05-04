package com.lorenzocensi.noteai.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lorenzocensi.noteai.R
import com.lorenzocensi.noteai.ui.components.PaperBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel = hiltViewModel()
) {
    val key by vm.key.collectAsStateWithLifecycle()
    val verify by vm.verify.collectAsStateWithLifecycle()
    var visible by remember { mutableStateOf(false) }

    PaperBackground(showRuledLines = false, showMargin = false) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = key,
                            onValueChange = vm::onKeyChange,
                            label = { Text(stringResource(R.string.settings_api_key_label)) },
                            placeholder = { Text(stringResource(R.string.settings_api_key_hint)) },
                            singleLine = true,
                            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { visible = !visible }) {
                                    Icon(
                                        imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { vm.saveKey() }, enabled = key.isNotBlank()) {
                                Text(stringResource(R.string.settings_api_key_save))
                            }
                            OutlinedButton(
                                onClick = { vm.verifyKey() },
                                enabled = key.isNotBlank() && verify !is VerifyState.Verifying
                            ) {
                                Text(stringResource(R.string.settings_verify_key))
                            }
                        }

                        when (val v = verify) {
                            VerifyState.Idle -> Unit
                            VerifyState.Verifying -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            VerifyState.Ok -> Text(
                                text = stringResource(R.string.settings_verify_ok),
                                color = MaterialTheme.colorScheme.primary
                            )
                            is VerifyState.Error -> Text(
                                text = "${stringResource(R.string.settings_verify_fail)} (${v.message})",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
