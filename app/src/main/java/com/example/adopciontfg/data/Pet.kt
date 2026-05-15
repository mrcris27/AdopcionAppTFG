package com.example.adopciontfg.data

data class Pet(
    val id: String,
    val name: String,
    val age: Int,
    val breed: String,
    val gender: String,
    val photos: List<String> = emptyList()

)