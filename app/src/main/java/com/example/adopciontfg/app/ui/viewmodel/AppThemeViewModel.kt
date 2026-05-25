package com.example.adopciontfg.app.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.data.settings.LaunchThemeController
import com.example.adopciontfg.domain.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext context: Context,
) : ViewModel() {
    private val authPreferences = context.getSharedPreferences(AUTH_PREFERENCES, Context.MODE_PRIVATE)

    private val _darkModeEnabled = MutableStateFlow(LaunchThemeController.loadConfiguredDarkMode(context))
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                roleFlow(),
                settingsRepository.userSettings(),
                settingsRepository.shelterSettings(),
            ) { role, userSettings, shelterSettings ->
                if (role == SHELTER_ROLE) {
                    shelterSettings.darkModeEnabled
                } else {
                    userSettings.darkModeEnabled
                }
            }
                .distinctUntilChanged()
                .collectLatest { enabled -> _darkModeEnabled.value = enabled }
        }
    }

    private fun roleFlow() = callbackFlow {
        fun currentRole(): String = authPreferences.getString(ROLE_KEY, USER_ROLE) ?: USER_ROLE

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == ROLE_KEY) {
                trySend(currentRole())
            }
        }

        trySend(currentRole())
        authPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            authPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    private companion object {
        const val AUTH_PREFERENCES = "auth"
        const val ROLE_KEY = "role"
        const val USER_ROLE = "user"
        const val SHELTER_ROLE = "shelter"
    }
}
