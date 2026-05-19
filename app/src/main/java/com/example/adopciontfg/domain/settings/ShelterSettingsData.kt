package com.example.adopciontfg.domain.settings

data class ShelterSettingsData(
    val shelterName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val cif: String = "",
    val profilePhotoUri: String = "",
    val adoptionAlertsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false
)
