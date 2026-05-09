package com.example.adopciontfg.app.ui.screens.shelter_profile_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.adopciontfg.app.ui.screens.components.CardViewList
import com.example.adopciontfg.app.ui.screens.components.ListCardView

@Preview(showBackground = true)
@Composable
fun AdoptionList() {

    val list = listOf(
        "Animal1",
        "Animal2",
        "Animal3",
        "Animal4",
        "Animal5"
    )

    ListCardView(
        items = list,
        onItemClick = { name ->
            println(name)
        },
        itemContent = { name, onClick ->
            CardViewList(
                name = name,
                onClick = onClick
            )
        }
    )
}