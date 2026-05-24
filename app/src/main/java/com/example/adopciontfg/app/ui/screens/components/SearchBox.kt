package com.example.adopciontfg.app.ui.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.adopciontfg.R
import com.example.adopciontfg.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSection(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = stringResource(R.string.buscar_animales),
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        placeholder = placeholder,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingSm)
    )
}
