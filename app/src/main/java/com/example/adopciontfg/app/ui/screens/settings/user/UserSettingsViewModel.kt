package com.example.adopciontfg.app.ui.screens.settings.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.domain.settings.SettingsRepository
import com.example.adopciontfg.domain.settings.UserSettingsData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserSettingsUiState(
    val name: String = "Usuario demo",
    val email: String = "user@adopcion.app",
    val phone: String = "",
    val city: String = "",
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val saveMessage: String? = null
)

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserSettingsUiState())
    val uiState: StateFlow<UserSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.userSettings().collectLatest { data ->
                _uiState.update { current ->
                    current.copy(
                        name = data.name,
                        email = data.email,
                        phone = data.phone,
                        city = data.city,
                        notificationsEnabled = data.notificationsEnabled,
                        darkModeEnabled = data.darkModeEnabled
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onCityChange(value: String) = _uiState.update { it.copy(city = value) }
    fun onNotificationsChange(enabled: Boolean) = _uiState.update { it.copy(notificationsEnabled = enabled) }
    fun onDarkModeChange(enabled: Boolean) = _uiState.update { it.copy(darkModeEnabled = enabled) }

    fun onSaveClick() {
        viewModelScope.launch {
            val current = _uiState.value
            settingsRepository.saveUserSettings(
                UserSettingsData(
                    name = current.name,
                    email = current.email,
                    phone = current.phone,
                    city = current.city,
                    notificationsEnabled = current.notificationsEnabled,
                    darkModeEnabled = current.darkModeEnabled
                )
            )
            _uiState.update { it.copy(saveMessage = "Cambios guardados") }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(saveMessage = null) }
    }
}
