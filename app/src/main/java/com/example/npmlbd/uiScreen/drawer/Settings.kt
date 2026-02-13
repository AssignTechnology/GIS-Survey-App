package com.example.npmlbd.uiScreen.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.npmlbd.component.AppTopBar
import com.example.npmlbd.ui.theme.ThemeMode
import com.example.npmlbd.ui.theme.ThemeViewModel
import com.example.npmlbd.ui.theme.ThemeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    navController: NavHostController,
) {

    AppTopBar(
        title = "Settings",
        onNavigationIconClick = {
            navController.popBackStack()
        },
        rememberNavController = navController,
        content = {
            SettingsScreen(it)
        }
    )
}
@Composable
fun SettingsScreen(values: PaddingValues) {
    val viewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(LocalContext.current))

    var showThemeDialog by remember { mutableStateOf(false) }
    val themeMode by viewModel.themeMode
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setThemeMode(ThemeMode.LIGHT) }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Light Theme")
                        Switch(
                            checked = themeMode == ThemeMode.LIGHT,
                            onCheckedChange = { if (it) viewModel.setThemeMode(ThemeMode.LIGHT) }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setThemeMode(ThemeMode.DARK) }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark Theme")
                        Switch(
                            checked = themeMode == ThemeMode.DARK,
                            onCheckedChange = { if (it) viewModel.setThemeMode(ThemeMode.DARK) }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setThemeMode(ThemeMode.SYSTEM) }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("System Theme")
                        Switch(
                            checked = themeMode == ThemeMode.SYSTEM,
                            onCheckedChange = { if (it) viewModel.setThemeMode(ThemeMode.SYSTEM) }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(values)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Theme Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showThemeDialog = true }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Change the app's theme",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Language Card (Placeholder)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: Add language selection logic */ }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Language",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Change the app's language",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}