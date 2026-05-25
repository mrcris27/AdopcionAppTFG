package com.example.adopciontfg.data.settings

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import java.io.IOException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object LaunchThemeController {
    private val USER_DARK_MODE = booleanPreferencesKey("user_dark_mode")
    private val SHELTER_DARK_MODE = booleanPreferencesKey("shelter_dark_mode")

    fun loadConfiguredDarkMode(context: Context): Boolean = runBlocking {
        context.settingsDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()
            .let { prefs ->
                val darkModeKey = if (currentRole(context) == SHELTER_ROLE) {
                    SHELTER_DARK_MODE
                } else {
                    USER_DARK_MODE
                }
                prefs[darkModeKey] ?: false
            }
    }

    fun applyConfiguredNightMode(context: Context): Boolean {
        val darkModeEnabled = loadConfiguredDarkMode(context)
        applyApplicationNightMode(context, darkModeEnabled)
        return darkModeEnabled
    }

    fun applyApplicationNightMode(context: Context, darkModeEnabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(UiModeManager::class.java)
                .setApplicationNightMode(
                    if (darkModeEnabled) {
                        UiModeManager.MODE_NIGHT_YES
                    } else {
                        UiModeManager.MODE_NIGHT_NO
                    }
                )
        }
    }

    private fun currentRole(context: Context): String {
        return context
            .getSharedPreferences(AUTH_PREFERENCES, Context.MODE_PRIVATE)
            .getString(ROLE_KEY, USER_ROLE)
            ?: USER_ROLE
    }

    private const val AUTH_PREFERENCES = "auth"
    private const val ROLE_KEY = "role"
    private const val USER_ROLE = "user"
    private const val SHELTER_ROLE = "shelter"
}
