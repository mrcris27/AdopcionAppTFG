package com.example.adopciontfg.domain.settings

data class UserSettingsData(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val biography: String = "",
    val profilePhotoUri: String = "",
    val darkModeEnabled: Boolean = false
)
