package com.example.adopciontfg.data.local.entity

import java.util.concurrent.TimeUnit
import kotlin.math.max

fun AnimalEntity.displayPhotos(): List<String> {
    val gallery = photos?.filter { it.isNotBlank() }.orEmpty()
    if (gallery.isNotEmpty()) return gallery
    val main = mainPhoto?.takeIf { it.isNotBlank() }
    return if (main != null) listOf(main) else emptyList()
}

fun AnimalEntity.ageInYears(): Int {
    if (birthDate <= 0L) return 0
    val years = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - birthDate) / 365
    return max(0, years.toInt())
}

fun AnimalEntity.genderLabel(): String = if (isSex) "Hembra" else "Macho"

fun AnimalEntity.formattedCharacteristics(): String =
    characteristics.orEmpty().joinToString(", ") { characteristic ->
        characteristic.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }
