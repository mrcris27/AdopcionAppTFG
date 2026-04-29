package com.example.adopciontfg.app.ui.screens.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> ListCardView(
    items: List<T>,
    onItemClick: (T) -> Unit,
    itemContent: @Composable (T, () -> Unit) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            itemContent(item) {
                onItemClick(item)
            }
        }
    }
}