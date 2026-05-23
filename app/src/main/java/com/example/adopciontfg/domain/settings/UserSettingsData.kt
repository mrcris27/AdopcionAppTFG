package com.example.adopciontfg.domain.settings

data class UserSettingsData(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val biography: String = "",
    val profilePhotoUri: String = "",
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false
)
