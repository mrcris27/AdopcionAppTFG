package com.example.adopciontfg.app.ui.screens.user_home_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.adopciontfg.app.ui.screens.components.ComponentList


@Preview(showBackground = true)
@Composable
fun ListCardView() {

    val list = listOf(
        "Protectora1",
        "Protectora2",
        "Protectora3",
        "Protectora4",
        "Protectora5"
    )

    ComponentList(
        items = list,
        onItemClick = { name ->
            // aquí luego navegación o acción
        }
    )
}