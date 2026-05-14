package com.example.adopciontfg.app.ui.screens.settings.shelter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopciontfg.domain.settings.SettingsRepository
import com.example.adopciontfg.domain.settings.ShelterSettingsData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShelterSettingsUiState(
    val shelterName: String = "Protectora demo",
    val email: String = "protectora@adopcion.app",
    val phone: String = "",
    val address: String = "",
    val cif: String = "",
    val adoptionAlertsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val saveMessage: String? = null
)

@HiltViewModel
class ShelterSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShelterSettingsUiState())
    val uiState: StateFlow<ShelterSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.shelterSettings().collectLatest { data ->
                _uiState.update { current ->
                    current.copy(
                        shelterName = data.shelterName,
                        email = data.email,
                        phone = data.phone,
                        address = data.address,
                        cif = data.cif,
                        adoptionAlertsEnabled = data.adoptionAlertsEnabled,
                        darkModeEnabled = data.darkModeEnabled
                    )
                }
            }
        }
    }

    fun onShelterNameChange(value: String) = _uiState.update { it.copy(shelterName = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onAddressChange(value: String) = _uiState.update { it.copy(address = value) }
    fun onCifChange(value: String) = _uiState.update { it.copy(cif = value) }
    
    fun onAdoptionAlertsChange(enabled: Boolean) = _uiState.update { it.copy(adoptionAlertsEnabled = enabled) }
    
    fun onDarkModeChange(enabled: Boolean) {
        _uiState.update { it.copy(darkModeEnabled = enabled) }
        // Guardar inmediatamente el cambio de tema
        viewModelScope.launch {
            val current = _uiState.value
            settingsRepository.saveShelterSettings(
                ShelterSettingsData(
                    shelterName = current.shelterName,
                    email = current.email,
                    phone = current.phone,
                    address = current.address,
                    cif = current.cif,
                    adoptionAlertsEnabled = current.adoptionAlertsEnabled,
                    darkModeEnabled = enabled
                )
            )
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val current = _uiState.value
            settingsRepository.saveShelterSettings(
                ShelterSettingsData(
                    shelterName = current.shelterName,
                    email = current.email,
                    phone = current.phone,
                    address = current.address,
                    cif = current.cif,
                    adoptionAlertsEnabled = current.adoptionAlertsEnabled,
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
