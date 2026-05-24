package com.example.adopciontfg.app.ui.screens.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.adopciontfg.R
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.local.entity.ageInYears
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species

@Composable
fun animalListSubtitle(animal: AnimalEntity): String {
    val species = animal.species
    val speciesLabel = if (species != null) {
        speciesLabel(species)
    } else {
        ""
    }
    val age = animal.ageInYears()
    return when {
        speciesLabel.isNotBlank() && age > 0 -> {
            stringResource(R.string.animal_list_subtitle_species_age, speciesLabel, age)
        }
        speciesLabel.isNotBlank() -> speciesLabel
        age > 0 -> stringResource(R.string.animal_list_subtitle_age, age)
        else -> stringResource(R.string.ver_mas_informacion)
    }
}

@Composable
fun speciesLabel(species: Species): String = stringResource(
    when (species) {
        Species.PERRO -> R.string.species_perro
        Species.GATO -> R.string.species_gato
        Species.CONEJO -> R.string.species_conejo
        Species.RATA -> R.string.species_rata
        Species.CABALLO -> R.string.species_caballo
        Species.OTHER -> R.string.species_other
    }
)

@Composable
fun characteristicLabel(characteristic: Characteristic): String = stringResource(
    when (characteristic) {
        Characteristic.SOCIABLE_CON_GATOS -> R.string.characteristic_sociable_con_gatos
        Characteristic.SOCIABLE_CON_PERROS -> R.string.characteristic_sociable_con_perros
        Characteristic.SOCIABLE_CON_GATO_Y_PERRO -> R.string.characteristic_sociable_con_gato_y_perro
        Characteristic.SOCIABLE_CON_KIDS -> R.string.characteristic_sociable_con_kids
        Characteristic.TRANQUILO -> R.string.characteristic_tranquilo
        Characteristic.JUGUETON -> R.string.characteristic_jugueton
        Characteristic.ENERGETICO -> R.string.characteristic_energetico
        Characteristic.REACTIVO -> R.string.characteristic_reactivo
    }
)
