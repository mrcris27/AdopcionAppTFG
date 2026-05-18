package com.example.adopciontfg.domain.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun userSettings(): Flow<UserSettingsData>
    suspend fun saveUserSettings(data: UserSettingsData)

    fun shelterSettings(): Flow<ShelterSettingsData>
    suspend fun saveShelterSettings(data: ShelterSettingsData)
}
