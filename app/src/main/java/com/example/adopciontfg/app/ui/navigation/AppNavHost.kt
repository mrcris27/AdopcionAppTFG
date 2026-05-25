package com.example.adopciontfg.app.ui.navigation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.auth.welcome.navigation.LoginResRoute
import com.example.adopciontfg.app.ui.screens.auth.welcome.navigation.loginResScreen
import com.example.adopciontfg.app.ui.screens.auth.login.navigation.LoginScreenRoute
import com.example.adopciontfg.app.ui.screens.auth.login.navigation.loginScreen
import com.example.adopciontfg.app.ui.screens.auth.register.navigation.RegistrationScreenRoute
import com.example.adopciontfg.app.ui.screens.auth.register.navigation.registrationScreen
import com.example.adopciontfg.app.ui.screens.components.SavingOverlay
import com.example.adopciontfg.app.ui.screens.shelter.registration.navigation.ShelterRegistrationRoute
import com.example.adopciontfg.app.ui.screens.shelter.registration.navigation.shelterRegistrationScreen
import com.example.adopciontfg.app.ui.screens.shelter.home.ShelterHomeScreen
import com.example.adopciontfg.app.ui.screens.splash.navigation.SplashRoute
import com.example.adopciontfg.app.ui.screens.splash.navigation.splashScreen
import com.example.adopciontfg.app.ui.screens.user.registration.navigation.UserRegistrationRoute
import com.example.adopciontfg.app.ui.screens.user.registration.navigation.userRegistrationScreen
import com.example.adopciontfg.app.ui.screens.user.settings.navigation.userSettingsScreen
import com.example.adopciontfg.data.remote.FirebaseService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val firebaseService = FirebaseService.getInstance()
    val coroutineScope = rememberCoroutineScope()
    var isLoggingOut by remember { mutableStateOf(false) }

    val startDestination = if (firebaseService.isLoggedIn()) {
        val role = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("role", "user")
        if (role == "shelter") ShelterMainRoute else UserMainRoute
    } else {
        LoginResRoute
    }

    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val isAuthLoading = authState is AuthViewModel.AuthState.Loading

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

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = SplashRoute
        ) {

            /* ---------------- AUTH ---------------- */

            splashScreen(
                onFinished = {
                    navController.navigate(LoginResRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )

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
                isLoading = isAuthLoading,
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
                isSaving = isAuthLoading,
            )

            shelterRegistrationScreen(
                onRegisterClick = { name, cif, phone, address, profilePhotoUri, email, adoptionFormUrl, password ->
                    authViewModel.registerShelter(
                        name = name,
                        cif = cif,
                        phoneName = phone,
                        address = address,
                        profilePic = profilePhotoUri,
                        email = email,
                        adoptionFormUrl = adoptionFormUrl,
                        password = password
                    )
                },
                onBackClick = { navController.popBackStack() },
                isSaving = isAuthLoading,
            )

            /* ---------------- MAIN ---------------- */

            composable<UserMainRoute> {
                AppScaffold(
                    onLogout = {
                        showLogoutOverlayAndNavigate(
                            isLoggingOut = isLoggingOut,
                            setLoggingOut = { isLoggingOut = it },
                            coroutineScope = coroutineScope,
                            onNavigate = { navigateToLogin(navController) },
                        )
                    },
                )
            }

            composable<ShelterMainRoute> {
                ShelterHomeScreen(
                    onLogout = {
                        showLogoutOverlayAndNavigate(
                            isLoggingOut = isLoggingOut,
                            setLoggingOut = { isLoggingOut = it },
                            coroutineScope = coroutineScope,
                            onNavigate = { navigateToLogin(navController, fromShelter = true) },
                        )
                    },
                )
            }
        }

        if (isLoggingOut) {
            SavingOverlay(message = stringResource(R.string.logging_out))
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

private fun showLogoutOverlayAndNavigate(
    isLoggingOut: Boolean,
    setLoggingOut: (Boolean) -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onNavigate: () -> Unit,
) {
    if (isLoggingOut) return

    setLoggingOut(true)
    coroutineScope.launch {
        delay(450)
        onNavigate()
        setLoggingOut(false)
    }
}