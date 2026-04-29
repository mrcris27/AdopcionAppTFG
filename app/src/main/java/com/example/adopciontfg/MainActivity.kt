package com.example.adopciontfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.adopciontfg.app.ui.navigation.AppScaffold
import com.example.adopciontfg.app.ui.navigation.ScreensNavHost
import com.example.adopciontfg.app.ui.screens.user_home_screen.UserHomeScreen
import com.example.adopciontfg.ui.theme.AdoptionTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Configuration.getInstance().userAgentValue = packageName
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdoptionTheme {
                val navController = rememberNavController()

                AppScaffold(navController = navController)

            }
        }
    }
}

