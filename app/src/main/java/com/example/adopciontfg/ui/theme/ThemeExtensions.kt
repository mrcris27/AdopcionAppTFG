package com.example.adopciontfg.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private fun ColorScheme.isDarkTheme(): Boolean = background.luminance() < 0.5f

/** Divider legible en fondos claros y oscuros. */
fun ColorScheme.subtleDivider(): Color =
    outline.copy(alpha = if (isDarkTheme()) 0.38f else 0.18f)

/** Superficie elevada (tarjetas, barras) con más contraste en modo oscuro. */
fun ColorScheme.elevatedSurface(): Color =
    if (isDarkTheme()) surfaceContainerHigh else surface

/** Borde de campos de texto sin foco. */
fun ColorScheme.inputOutlineUnfocused(): Color =
    outline.copy(alpha = if (isDarkTheme()) 0.62f else 0.4f)
