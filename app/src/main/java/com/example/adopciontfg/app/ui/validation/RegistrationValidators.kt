package com.example.adopciontfg.app.ui.validation

import android.util.Patterns
import java.net.URI

const val MIN_PASSWORD_LENGTH = 6

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
}

fun isValidRegistrationPassword(password: String): Boolean {
    return password.length >= MIN_PASSWORD_LENGTH
}

fun isValidSpanishPhone(phone: String): Boolean {
    val normalizedPhone = phone.trim()
        .replace(" ", "")
        .replace("-", "")

    return Regex("""^(?:(?:\+34|0034)?[6789]\d{8})$""").matches(normalizedPhone)
}

fun isValidGoogleFormsUrl(url: String): Boolean {
    val parsedUri = runCatching { URI(url.trim()) }.getOrNull() ?: return false
    val scheme = parsedUri.scheme?.lowercase()
    val host = parsedUri.host?.lowercase()?.removePrefix("www.") ?: return false
    val path = parsedUri.path.orEmpty()

    val isShortFormsLink = host == "forms.gle" && path.length > 1
    val isDocsFormsLink = host == "docs.google.com" && path.startsWith("/forms/")

    return scheme == "https" && (isShortFormsLink || isDocsFormsLink)
}

fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
    return password == confirmPassword
}
