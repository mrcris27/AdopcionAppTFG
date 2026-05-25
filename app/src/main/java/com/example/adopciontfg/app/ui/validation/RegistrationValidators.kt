package com.example.adopciontfg.app.ui.validation

import android.util.Patterns

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

fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
    return password == confirmPassword
}
