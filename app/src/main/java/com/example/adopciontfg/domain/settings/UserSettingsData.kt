package com.example.adopciontfg.domain.settings

data class UserSettingsData(
    val name: String = "Usuario demo",
    val email: String = "user@adopcion.app",
    val phone: String = "",
    val city: String = "",
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false
)
