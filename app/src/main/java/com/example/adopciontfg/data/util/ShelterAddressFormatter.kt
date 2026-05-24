package com.example.adopciontfg.data.util

data class ShelterAddressParts(
    val street: String = "",
    val streetNumber: String = "",
    val postalCode: String = "",
    val city: String = "",
    val province: String = "",
)

fun buildShelterAddress(parts: ShelterAddressParts): String {
    val streetLine = listOf(parts.street, parts.streetNumber)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString(", ")
    val cityLine = listOf(parts.postalCode, parts.city)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString(" ")

    return listOf(streetLine, cityLine, parts.province.trim(), "España")
        .filter { it.isNotBlank() }
        .joinToString(", ")
}

fun parseShelterAddress(address: String): ShelterAddressParts {
    val parts = address.split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }

    if (parts.size < 4) return ShelterAddressParts()

    val cityLine = parts.getOrNull(2).orEmpty()
    val postalCode = cityLine.takeWhile { it.isDigit() }
    val city = cityLine.removePrefix(postalCode).trim()

    return ShelterAddressParts(
        street = parts.getOrNull(0).orEmpty(),
        streetNumber = parts.getOrNull(1).orEmpty(),
        postalCode = postalCode,
        city = city,
        province = parts.getOrNull(3).orEmpty(),
    )
}
