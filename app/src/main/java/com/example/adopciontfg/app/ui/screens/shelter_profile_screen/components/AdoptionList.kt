package com.example.adopciontfg.app.ui.screens.shelter_profile_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.adopciontfg.app.ui.screens.components.ComponentList

@Preview
@Composable
fun AdoptionList () {


    val list = listOf(
        "Animal1",
        "Animal2",
        "Animal3",
        "Animal4",
        "Animal5"
    )

    ComponentList(
        items = list,
        onItemClick = { name ->
            // aquí luego navegación o acción
        }
    )
}