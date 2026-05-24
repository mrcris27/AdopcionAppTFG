package com.example.adopciontfg.app.ui.screens.user.shelter_profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.adopciontfg.app.ui.screens.components.CardViewList
import com.example.adopciontfg.app.ui.screens.components.ListCardView

@Composable
fun SponsorList(
    animals: List<String> = emptyList(),
    onAnimalClick: (String) -> Unit = {},
) {
    ListCardView(
        items = animals,
        onItemClick = onAnimalClick,
        itemContent = { name, onClick ->
            CardViewList(
                name = name,
                onClick = onClick
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SponsorListPreview() {
    SponsorList()
}