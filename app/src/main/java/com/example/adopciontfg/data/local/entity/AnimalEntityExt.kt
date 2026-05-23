package com.example.adopciontfg.data.local.entity

import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Funciones de extensión para [AnimalEntity].
 *
 * [AnimalEntity] es la entidad de Room que guarda los datos crudos del animal (IDs, URLs,
 * timestamps, enums, etc.). Estas extensiones convierten esos datos en textos y listas listos
 * para mostrar en Compose, sin duplicar lógica en cada pantalla.
 *
 * Se usan principalmente en:
 * - [com.example.adopciontfg.app.ui.screens.user.pet_detail.PetDetailScreen] (detalle)
 * - [com.example.adopciontfg.app.ui.screens.user.pet_list.PetListScreen] y perfil de protectora (lista)
 */

/**
 * Devuelve las URLs de fotos que debe mostrar la UI.
 *
 * Prioridad:
 * 1. [AnimalEntity.photos] — galería completa, sin entradas vacías.
 * 2. [AnimalEntity.mainPhoto] — si no hay galería, usa solo la foto principal.
 * 3. Lista vacía — la pantalla puede mostrar un placeholder ("Sin fotos disponibles").
 */
fun AnimalEntity.displayPhotos(): List<String> {
    val gallery = photos?.filter { it.isNotBlank() }.orEmpty()
    if (gallery.isNotEmpty()) return gallery
    val main = mainPhoto?.takeIf { it.isNotBlank() }
    return if (main != null) listOf(main) else emptyList()
}

/**
 * Calcula la edad del animal en años a partir de [AnimalEntity.birthDate].
 *
 * [birthDate] es un timestamp en milisegundos (como guarda Room/Firebase).
 * Si no hay fecha válida (`birthDate <= 0`), devuelve `0`.
 */
fun AnimalEntity.ageInYears(): Int {
    if (birthDate <= 0L) return 0
    val years = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - birthDate) / 365
    return max(0, years.toInt())
}

/**
 * Etiqueta de género legible para el usuario.
 *
 * En la base de datos, [AnimalEntity.isSex] es un booleano:
 * - `true` → "Hembra"
 * - `false` → "Macho"
 */
fun AnimalEntity.genderLabel(): String = if (isSex) "Hembra" else "Macho"

/** Animales visibles en listados de usuarios. */
fun AnimalEntity.isPubliclyVisible(): Boolean = isForAdoption

fun AnimalEntity.statusLabel(): String = if (isForAdoption) "Disponible" else "No disponible"

/**
 * Convierte la lista de [com.example.adopciontfg.model.Characteristic] en un único texto
 * separado por comas, con nombres legibles.
 *
 * Ejemplo: `SOCIABLE_CON_PERROS` → "Sociable con perros".
 */
fun AnimalEntity.formattedCharacteristics(): String =
    characteristics.orEmpty().joinToString(", ") { characteristic ->
        characteristic.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }

/**
 * Subtítulo corto para tarjetas de la lista (especie y edad).
 *
 * Ejemplos de salida:
 * - "Perro · 3 años" (si hay especie y edad)
 * - "Perro" (solo especie)
 * - "3 años" (solo edad)
 * - "Ver detalles" (si faltan ambos datos)
 */
fun AnimalEntity.listSubtitle(): String {
    val speciesLabel = species?.name
        ?.replace("_", " ")
        ?.lowercase()
        ?.replaceFirstChar { it.uppercase() }
        .orEmpty()
    val age = ageInYears()
    return when {
        speciesLabel.isNotBlank() && age > 0 -> "$speciesLabel · $age años"
        speciesLabel.isNotBlank() -> speciesLabel
        age > 0 -> "$age años"
        else -> "Ver detalles"
    }
}
