package com.example.adopciontfg.app.ui.screens.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.splash.AnimatedSplashScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.splashScreen(
    onFinished: () -> Unit,
) {
    composable<SplashRoute> {
        AnimatedSplashScreen(onFinished = onFinished)
    }
}

@Serializable
object SplashRoute
