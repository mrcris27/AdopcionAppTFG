package com.example.adopciontfg.app.ui.screens.shelter.registration

import androidx.lifecycle.ViewModel
import com.example.adopciontfg.data.util.ShelterAddressParts
import com.example.adopciontfg.data.util.buildShelterAddress
import com.example.adopciontfg.app.ui.validation.doPasswordsMatch
import com.example.adopciontfg.app.ui.validation.isValidEmail
import com.example.adopciontfg.app.ui.validation.isValidRegistrationPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ShelterRegistrationUiState(
    val name: String = "",
    val cif: String = "",
    val phone: String = "",
    val address: String = "",
    val street: String = "",
    val streetNumber: String = "",
    val postalCode: String = "",
    val city: String = "",
    val province: String = "",
    val email: String = "",
    val profilePhotoUri: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordHidden: Boolean = true,
    val confirmPasswordHidden: Boolean = true,
    val canRegister: Boolean = false
) {
    val isEmailInvalid: Boolean
        get() = email.isNotBlank() && !isValidEmail(email)

    val isPasswordInvalid: Boolean
        get() = password.isNotBlank() && !isValidRegistrationPassword(password)

    val doPasswordsNotMatch: Boolean
        get() = confirmPassword.isNotBlank() && !doPasswordsMatch(password, confirmPassword)
}

@HiltViewModel
class ShelterRegistrationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ShelterRegistrationUiState())
    val uiState: StateFlow<ShelterRegistrationUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) = updateForm { it.copy(name = name) }
    fun onCifChange(cif: String) = updateForm { it.copy(cif = cif) }
    fun onPhoneChange(phone: String) = updateForm { it.copy(phone = phone) }
    fun onStreetChange(street: String) = updateAddress { it.copy(street = street) }
    fun onStreetNumberChange(streetNumber: String) = updateAddress { it.copy(streetNumber = streetNumber) }
    fun onPostalCodeChange(postalCode: String) =
        updateAddress { it.copy(postalCode = postalCode.filter { char -> char.isDigit() }.take(5)) }
    fun onCityChange(city: String) = updateAddress { it.copy(city = city) }
    fun onProvinceChange(province: String) = updateAddress { it.copy(province = province) }
    fun onEmailChange(email: String) = updateForm { it.copy(email = email) }
    fun onProfilePhotoChange(value: String) = _uiState.update { it.copy(profilePhotoUri = value) }
    fun onPasswordChange(password: String) = updateForm { it.copy(password = password) }
    fun onConfirmPasswordChange(confirmPassword: String) =
        updateForm { it.copy(confirmPassword = confirmPassword) }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordHidden = !it.passwordHidden) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordHidden = !it.confirmPasswordHidden) }
    }

    private fun updateForm(transform: (ShelterRegistrationUiState) -> ShelterRegistrationUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            updated.copy(canRegister = validate(updated))
        }
    }

    private fun updateAddress(transform: (ShelterRegistrationUiState) -> ShelterRegistrationUiState) {
        updateForm { current ->
            val updated = transform(current)
            updated.copy(address = buildFullAddress(updated))
        }
    }

    private fun buildFullAddress(state: ShelterRegistrationUiState): String {
        return buildShelterAddress(
            ShelterAddressParts(
                street = state.street,
                streetNumber = state.streetNumber,
                postalCode = state.postalCode,
                city = state.city,
                province = state.province,
            )
        )
    }

    private fun validate(state: ShelterRegistrationUiState): Boolean {
        return state.name.isNotBlank() &&
            state.cif.isNotBlank() &&
            state.phone.isNotBlank() &&
            state.street.isNotBlank() &&
            state.streetNumber.isNotBlank() &&
            state.postalCode.length == 5 &&
            state.postalCode.all { it.isDigit() } &&
            state.city.isNotBlank() &&
            state.province.isNotBlank() &&
            isValidEmail(state.email) &&
            isValidRegistrationPassword(state.password) &&
            doPasswordsMatch(state.password, state.confirmPassword)
    }
}
