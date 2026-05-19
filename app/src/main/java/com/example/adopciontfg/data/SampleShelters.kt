package com.example.adopciontfg.data

import com.example.adopciontfg.data.local.entity.ShelterEntity

val sampleShelters: List<ShelterEntity> = listOf(
    ShelterEntity(
        "1",
        "Huellas Madrid",
        "G12345678",
        "",
        "contacto@huellasmadrid.org",
        "Puerta del Sol, 1, Madrid",
        "912 345 678",
        "https://forms.gle/ejemplo-huellas",
    ),
    ShelterEntity(
        "2",
        "Peludos del Centro",
        "G87654321",
        "",
        "info@peludoscentro.es",
        "Plaza de Cibeles, Madrid",
        "913 456 789",
        "https://forms.gle/ejemplo-peludos",
    ),
    ShelterEntity(
        "3",
        "Amigos del Prado",
        "G11223344",
        "",
        "adopciones@amigosdelprado.org",
        "Paseo del Prado, 1, Madrid",
        "914 567 890",
        "https://forms.gle/ejemplo-prado",
    ),
)
