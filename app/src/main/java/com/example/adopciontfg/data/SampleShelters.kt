package com.example.adopciontfg.data

val sampleShelters: List<Shelter> = listOf(
    Shelter("1", "Protectora 1", 40.4168, -3.7038),
    Shelter("2", "Protectora 2", 40.45, -3.70),
    Shelter("3", "Protectora 3", 40.40, -3.65)
)

fun shelterByIdOrDefault(id: String): Shelter =
    sampleShelters.find { it.id == id } ?: sampleShelters.first()
