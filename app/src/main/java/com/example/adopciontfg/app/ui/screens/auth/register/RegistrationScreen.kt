package com.example.adopciontfg.app.ui.screens.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.components.AppPrimaryButton
import com.example.adopciontfg.app.ui.screens.components.AppSecondaryButton
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onBackClick: () -> Unit,
    onRegisterUserClick: () -> Unit,
    onRegisterShelterClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopAppBar(
                title = stringResource(R.string.registration),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.screenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.elevatedSurface()
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = Dimens.spacingXs)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.cardPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
                ) {
                    Text(
                        text = stringResource(R.string.registration_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )

                    AppPrimaryButton(
                        text = stringResource(R.string.i_am_user),
                        onClick = onRegisterUserClick
                    )
                    AppSecondaryButton(
                        text = stringResource(R.string.i_am_shelter),
                        onClick = onRegisterShelterClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    AdoptionTheme {
        RegistrationScreen(
            onBackClick = {},
            onRegisterUserClick = {},
            onRegisterShelterClick = {}
        )
    }
}
