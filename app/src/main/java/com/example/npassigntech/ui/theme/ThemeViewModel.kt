package com.example.npassigntech.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
@SuppressLint("StaticFieldLeak")
class ThemeViewModel( private val context: Context) : ViewModel() {
    private val themeKey = stringPreferencesKey("theme_mode")
    val themeMode = mutableStateOf(ThemeMode.SYSTEM)

    init {
        viewModelScope.launch {
            context.dataStore.data.map { preferences ->
                val savedTheme = preferences[themeKey] ?: ThemeMode.SYSTEM.name
                ThemeMode.valueOf(savedTheme)
            }.collect { mode ->
                themeMode.value = mode
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[themeKey] = mode.name
            }
            themeMode.value = mode
        }
    }
}