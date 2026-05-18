package com.example.adopciontfg.ui.theme



import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.darkColorScheme

import androidx.compose.material3.lightColorScheme

import androidx.compose.runtime.Composable



private val LightColors = lightColorScheme(

    primary = PrimaryLight,

    onPrimary = OnPrimaryLight,

    primaryContainer = PrimaryContainerLight,

    onPrimaryContainer = OnPrimaryContainerLight,

    secondary = SecondaryLight,

    onSecondary = OnSecondaryLight,

    secondaryContainer = SecondaryContainerLight,

    onSecondaryContainer = OnSecondaryContainerLight,

    background = BackgroundLight,

    onBackground = OnBackgroundLight,

    surface = SurfaceLight,

    onSurface = OnSurfaceLight,

    surfaceVariant = SurfaceVariantLight,

    onSurfaceVariant = OnSurfaceVariantLight,

    outline = OutlineLight,

    surfaceContainerLow = SurfaceContainerLowLight,

    surfaceContainer = SurfaceContainerLight,

    surfaceContainerHigh = SurfaceContainerHighLight,

    surfaceContainerHighest = SurfaceContainerHighestLight,

)



private val DarkColors = darkColorScheme(

    primary = PrimaryDark,

    onPrimary = OnPrimaryDark,

    primaryContainer = PrimaryContainerDark,

    onPrimaryContainer = OnPrimaryContainerDark,

    secondary = SecondaryDark,

    onSecondary = OnSecondaryDark,

    secondaryContainer = SecondaryContainerDark,

    onSecondaryContainer = OnSecondaryContainerDark,

    background = BackgroundDark,

    onBackground = OnBackgroundDark,

    surface = SurfaceDark,

    onSurface = OnSurfaceDark,

    surfaceVariant = SurfaceVariantDark,

    onSurfaceVariant = OnSurfaceVariantDark,

    outline = OutlineDark,

    surfaceContainerLow = SurfaceContainerLowDark,

    surfaceContainer = SurfaceContainerDark,

    surfaceContainerHigh = SurfaceContainerHighDark,

    surfaceContainerHighest = SurfaceContainerHighestDark,

)



@Composable

fun AdoptionTheme(

    darkTheme: Boolean = isSystemInDarkTheme(),

    content: @Composable () -> Unit

) {

    MaterialTheme(

        colorScheme = if (darkTheme) DarkColors else LightColors,

        typography = Typography,

        shapes = Shapes,

        content = content

    )

}

