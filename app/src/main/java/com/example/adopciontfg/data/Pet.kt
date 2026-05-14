package com.example.adopciontfg.data

import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species

data class Pet(
    val id: String,
    val name: String,
    val age: Int,
    val breed: String,
    val gender: String,
    val photos: List<String> = emptyList(),
    val description: String = "",
    val species: Species = Species.PERRO,
    val characteristics: List<Characteristic> = emptyList(),
    val birthDate: Long = 0L,
    val mainPhoto: String = "",
    val shelterId: String = ""
)