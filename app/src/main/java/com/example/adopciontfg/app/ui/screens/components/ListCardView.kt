package com.example.adopciontfg.app.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.adopciontfg.ui.theme.Dimens

@Composable
fun <T> ListCardView(
    items: List<T>,
    onItemClick: (T) -> Unit,
    itemContent: @Composable (T, () -> Unit) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.listItemSpacing),
        contentPadding = PaddingValues(vertical = Dimens.spacingSm)
    ) {
        items(items) { item ->
            itemContent(item) {
                onItemClick(item)
            }
        }
    }
}
