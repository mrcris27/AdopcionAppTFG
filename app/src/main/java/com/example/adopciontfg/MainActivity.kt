package com.example.adopciontfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.adopciontfg.app.ui.navigation.AppNavHost
import com.example.adopciontfg.app.ui.viewmodel.AppThemeViewModel
import com.example.adopciontfg.data.settings.LaunchThemeController
import com.example.adopciontfg.ui.theme.AdoptionTheme
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Configuration.getInstance().userAgentValue = packageName
        LaunchThemeController.applyConfiguredNightMode(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeViewModel: AppThemeViewModel = viewModel()
            val darkModeEnabled = themeViewModel.darkModeEnabled.collectAsStateWithLifecycle()

            AdoptionTheme(darkTheme = darkModeEnabled.value) {
                val navController = rememberNavController()

                AppNavHost(navController = navController)
            }
        }
    }
}