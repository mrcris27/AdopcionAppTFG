package com.example.adopciontfg.domain.settings

data class ShelterSettingsData(
    val shelterName: String = "Protectora demo",
    val email: String = "protectora@adopcion.app",
    val phone: String = "",
    val address: String = "",
    val cif: String = "",
    val adoptionAlertsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false
)
