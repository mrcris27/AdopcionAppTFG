package com.example.adopciontfg.app.ui.screens.shelter_registration

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ShelterRegistrationUiState(
    val name: String = "",
    val cif: String = "",
    val tel: String = "",
    val address: String = "",
    val email: String = "",
    val password: String = "",
    val passwordHidden: Boolean = true,
    val canRegister: Boolean = false
)

@HiltViewModel
class ShelterRegistrationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ShelterRegistrationUiState())
    val uiState: StateFlow<ShelterRegistrationUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) = updateForm { it.copy(name = name) }
    fun onCifChange(cif: String) = updateForm { it.copy(cif = cif) }
    fun onTelChange(tel: String) = updateForm { it.copy(tel = tel) }
    fun onAddressChange(address: String) = updateForm { it.copy(address = address) }
    fun onEmailChange(email: String) = updateForm { it.copy(email = email) }
    fun onPasswordChange(password: String) = updateForm { it.copy(password = password) }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordHidden = !it.passwordHidden) }
    }

    fun onRegisterClick() {
        // Placeholder for UI-only registration actions (validation, analytics, etc).
    }

    private fun updateForm(transform: (ShelterRegistrationUiState) -> ShelterRegistrationUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            updated.copy(canRegister = validate(updated))
        }
    }

    private fun validate(state: ShelterRegistrationUiState): Boolean {
        return state.name.isNotBlank() &&
            state.cif.isNotBlank() &&
            state.tel.isNotBlank() &&
            state.address.isNotBlank() &&
            state.email.contains("@") &&
            state.password.length >= 6
    }
}
