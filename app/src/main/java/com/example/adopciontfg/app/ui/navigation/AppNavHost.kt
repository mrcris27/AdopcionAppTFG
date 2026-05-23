package com.example.adopciontfg.app.ui.navigation

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.adopciontfg.app.ui.screens.auth.welcome.navigation.LoginResRoute
import com.example.adopciontfg.app.ui.screens.auth.welcome.navigation.loginResScreen
import com.example.adopciontfg.app.ui.screens.auth.login.navigation.LoginScreenRoute
import com.example.adopciontfg.app.ui.screens.auth.login.navigation.loginScreen
import com.example.adopciontfg.app.ui.screens.auth.register.navigation.RegistrationScreenRoute
import com.example.adopciontfg.app.ui.screens.auth.register.navigation.registrationScreen
import com.example.adopciontfg.app.ui.screens.shelter.registration.navigation.ShelterRegistrationRoute
import com.example.adopciontfg.app.ui.screens.shelter.registration.navigation.shelterRegistrationScreen
import com.example.adopciontfg.app.ui.screens.shelter.home.ShelterHomeScreen
import com.example.adopciontfg.app.ui.screens.user.registration.navigation.UserRegistrationRoute
import com.example.adopciontfg.app.ui.screens.user.registration.navigation.userRegistrationScreen
import com.example.adopciontfg.app.ui.screens.user.settings.navigation.userSettingsScreen
import com.example.adopciontfg.data.remote.FirebaseService
import kotlinx.serialization.Serializable

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val firebaseService = FirebaseService.getInstance()

    val startDestination = if (firebaseService.isLoggedIn()) {
        val role = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("role", "user")
        if (role == "shelter") ShelterMainRoute else UserMainRoute
    } else {
        LoginResRoute
    }

    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthViewModel.AuthState.Success -> {
                val destination = if (state.role == "shelter") {
                    ShelterMainRoute
                } else {
                    UserMainRoute
                }

                navController.navigate(destination) {
                    popUpTo(LoginResRoute) { inclusive = true }
                }
                authViewModel.resetAuthState()
            }

            is AuthViewModel.AuthState.Error -> {
                Toast.makeText(
                    context,
                    state.message,
                    Toast.LENGTH_SHORT
                ).show()
                authViewModel.resetAuthState()
            }

            AuthViewModel.AuthState.Idle,
            AuthViewModel.AuthState.Loading -> Unit
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        /* ---------------- AUTH ---------------- */

        loginResScreen(
            onLoginClick = {
                authViewModel.resetAuthState()
                navController.navigate(LoginScreenRoute)
            },
            onRegisterClick = {
                authViewModel.resetAuthState()
                navController.navigate(RegistrationScreenRoute)
            }
        )

        loginScreen(
            onBackClick = { navController.popBackStack() },
            onContinueClick = { email, password ->
                authViewModel.login(email, password)
            },
            onShelterPreviewClick = {
                navController.navigate(ShelterMainRoute) {
                    popUpTo(LoginScreenRoute) { inclusive = true }
                }
            },
        )

        registrationScreen(
            onBackClick = { navController.popBackStack() },
            onRegisterUserClick = {
                authViewModel.resetAuthState()
                navController.navigate(UserRegistrationRoute)
            },
            onRegisterShelterClick = {
                authViewModel.resetAuthState()
                navController.navigate(ShelterRegistrationRoute)
            }
        )

        userRegistrationScreen(
            onBackClick = { navController.popBackStack() },
            onRegisterClick = { name, surname, email, biography, profilePhotoUri, password ->
                authViewModel.registerUser(
                    name = name,
                    surname = surname,
                    email = email,
                    bio = biography,
                    profilePic = profilePhotoUri,
                    password = password
                )
            },
        )

        shelterRegistrationScreen(
            onRegisterClick = { name, cif, phone, address, profilePhotoUri, email, password ->
                authViewModel.registerShelter(
                    name = name,
                    cif = cif,
                    phoneName = phone,
                    address = address,
                    profilePic = profilePhotoUri,
                    email = email,
                    password = password
                )
            },
            onBackClick = { navController.popBackStack() },
        )

        /* ---------------- MAIN ---------------- */

        composable<UserMainRoute> {
            AppScaffold(
                onLogout = { navigateToLogin(navController) },
            )
        }

        composable<ShelterMainRoute> {
            ShelterHomeScreen(
                onLogout = { navigateToLogin(navController, fromShelter = true) },
            )
        }
    }
}

@Serializable
private object UserMainRoute

@Serializable
private object ShelterMainRoute

private fun navigateToLogin(
    navController: NavHostController,
    fromShelter: Boolean = false,
) {
    val mainRoute = if (fromShelter) ShelterMainRoute else UserMainRoute
    navController.navigate(LoginResRoute) {
        popUpTo(mainRoute) { inclusive = true }
        launchSingleTop = true
    }
}