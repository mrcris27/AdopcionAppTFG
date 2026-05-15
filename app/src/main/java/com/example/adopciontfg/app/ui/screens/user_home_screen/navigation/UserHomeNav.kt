package com.example.adopciontfg.app.ui.screens.user_home_screen.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.user_home_screen.UserHomeScreen
import com.example.adopciontfg.data.local.entity.ShelterEntity
import kotlinx.serialization.Serializable

fun NavGraphBuilder.userScreen(
    onDetailClick: (ShelterEntity) -> Unit
) {
    composable<UserScreenRoute>{
        UserHomeScreen(
            modifier = Modifier.fillMaxSize(),
            onDetailClick = onDetailClick
        )
    }
}

@Serializable
object UserScreenRoute