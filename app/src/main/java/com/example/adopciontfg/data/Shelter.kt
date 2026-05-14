package com.example.adopciontfg.data

data class Shelter(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val cif: String = "",
    val profilePicture: String = "",
    val email: String = "",
    val address: String = "",
    val phone: String = ""
)