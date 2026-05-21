package com.example.adopciontfg.app.ui.screens.auth.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.components.AppPrimaryButton
import com.example.adopciontfg.app.ui.screens.components.AppSecondaryButton
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface

@Composable
fun LoginRegistrationScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.screenPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(132.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.elevatedSurface(),
                    shadowElevation = 6.dp,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.adopta_splash_logo),
                            contentDescription = null,
                            modifier = Modifier.size(116.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.spacingXl))

                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(Dimens.spacingSm))

                Text(
                    text = stringResource(R.string.app_tagline),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Dimens.spacingLg)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.spacingXxl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppPrimaryButton(
                    text = "Iniciar sesión",
                    onClick = onLoginClick
                )
                AppSecondaryButton(
                    text = "Registrarse",
                    onClick = onRegisterClick
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Composable
fun LoginRegistrationPreview() {
    AdoptionTheme(darkTheme = false) {
        LoginRegistrationScreen(onLoginClick = {}, onRegisterClick = {})
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
fun LoginRegistrationDarkPreview() {
    AdoptionTheme(darkTheme = true) {
        LoginRegistrationScreen(onLoginClick = {}, onRegisterClick = {})
    }
}
