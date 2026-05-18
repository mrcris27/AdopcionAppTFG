package com.example.adopciontfg.app.ui.screens.user_registration

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserRegistrationUiState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val biography: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordHidden: Boolean = true,
    val confirmPasswordHidden: Boolean = true,
    val canRegister: Boolean = false
)

@HiltViewModel
class UserRegistrationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UserRegistrationUiState())
    val uiState: StateFlow<UserRegistrationUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = updateForm { it.copy(name = value) }
    fun onSurnameChange(value: String) = updateForm { it.copy(surname = value) }
    fun onEmailChange(value: String) = updateForm { it.copy(email = value) }
    fun onBiographyChange(value: String) = updateForm { it.copy(biography = value) }
    fun onPasswordChange(value: String) = updateForm { it.copy(password = value) }
    fun onConfirmPasswordChange(value: String) = updateForm { it.copy(confirmPassword = value) }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordHidden = !it.passwordHidden) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordHidden = !it.confirmPasswordHidden) }
    }

    fun onRegisterClick() {
        // Placeholder for persistence / auth integration.
    }

    private fun updateForm(transform: (UserRegistrationUiState) -> UserRegistrationUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            updated.copy(canRegister = validate(updated))
        }
    }

    private fun validate(state: UserRegistrationUiState): Boolean {
        return state.name.isNotBlank() &&
            state.surname.isNotBlank() &&
            state.email.contains("@") &&
            state.password.length >= 6 &&
            state.password == state.confirmPassword
    }
}
