package com.example.adopciontfg.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.adopciontfg.domain.settings.SettingsRepository
import com.example.adopciontfg.domain.settings.ShelterSettingsData
import com.example.adopciontfg.domain.settings.UserSettingsData
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override fun userSettings(): Flow<UserSettingsData> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { prefs ->
                UserSettingsData(
                    name = prefs[USER_NAME] ?: "",
                    surname = prefs[USER_SURNAME] ?: "",
                    email = prefs[USER_EMAIL] ?: "",
                    phone = prefs[USER_PHONE] ?: "",
                    city = prefs[USER_CITY] ?: "",
                    biography = prefs[USER_BIOGRAPHY] ?: "",
                    profilePhotoUri = prefs[USER_PROFILE_PHOTO_URI] ?: "",
                    notificationsEnabled = prefs[USER_NOTIFICATIONS] ?: true,
                    darkModeEnabled = prefs[USER_DARK_MODE] ?: false
                )
            }
    }

    override suspend fun saveUserSettings(data: UserSettingsData) {
        dataStore.edit { prefs ->
            prefs[USER_NAME] = data.name
            prefs[USER_SURNAME] = data.surname
            prefs[USER_EMAIL] = data.email
            prefs[USER_PHONE] = data.phone
            prefs[USER_CITY] = data.city
            prefs[USER_BIOGRAPHY] = data.biography
            prefs[USER_PROFILE_PHOTO_URI] = data.profilePhotoUri
            prefs[USER_NOTIFICATIONS] = data.notificationsEnabled
            prefs[USER_DARK_MODE] = data.darkModeEnabled
        }
    }

    override fun shelterSettings(): Flow<ShelterSettingsData> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { prefs ->
                ShelterSettingsData(
                    shelterName = prefs[SHELTER_NAME] ?: "",
                    email = prefs[SHELTER_EMAIL] ?: "",
                    phone = prefs[SHELTER_PHONE] ?: "",
                    address = prefs[SHELTER_ADDRESS] ?: "",
                    cif = prefs[SHELTER_CIF] ?: "",
                    profilePhotoUri = prefs[SHELTER_PROFILE_PHOTO_URI] ?: "",
                    adoptionAlertsEnabled = prefs[SHELTER_ADOPTION_ALERTS] ?: true,
                    darkModeEnabled = prefs[SHELTER_DARK_MODE] ?: false
                )
            }
    }

    override suspend fun saveShelterSettings(data: ShelterSettingsData) {
        dataStore.edit { prefs ->
            prefs[SHELTER_NAME] = data.shelterName
            prefs[SHELTER_EMAIL] = data.email
            prefs[SHELTER_PHONE] = data.phone
            prefs[SHELTER_ADDRESS] = data.address
            prefs[SHELTER_CIF] = data.cif
            prefs[SHELTER_PROFILE_PHOTO_URI] = data.profilePhotoUri
            prefs[SHELTER_ADOPTION_ALERTS] = data.adoptionAlertsEnabled
            prefs[SHELTER_DARK_MODE] = data.darkModeEnabled
        }
    }

    private companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_SURNAME = stringPreferencesKey("user_surname")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val USER_CITY = stringPreferencesKey("user_city")
        val USER_BIOGRAPHY = stringPreferencesKey("user_biography")
        val USER_PROFILE_PHOTO_URI = stringPreferencesKey("user_profile_photo_uri")
        val USER_NOTIFICATIONS = booleanPreferencesKey("user_notifications")
        val USER_DARK_MODE = booleanPreferencesKey("user_dark_mode")

        val SHELTER_NAME = stringPreferencesKey("shelter_name")
        val SHELTER_EMAIL = stringPreferencesKey("shelter_email")
        val SHELTER_PHONE = stringPreferencesKey("shelter_phone")
        val SHELTER_ADDRESS = stringPreferencesKey("shelter_address")
        val SHELTER_CIF = stringPreferencesKey("shelter_cif")
        val SHELTER_PROFILE_PHOTO_URI = stringPreferencesKey("shelter_profile_photo_uri")
        val SHELTER_ADOPTION_ALERTS = booleanPreferencesKey("shelter_adoption_alerts")
        val SHELTER_DARK_MODE = booleanPreferencesKey("shelter_dark_mode")
    }
}
