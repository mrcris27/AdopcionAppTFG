package com.example.adopciontfg.app.ui.screens.auth.login

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.screens.components.AppFilledTextField
import com.example.adopciontfg.app.ui.screens.components.AppFormSection
import com.example.adopciontfg.app.ui.screens.components.AppPrimaryButton
import com.example.adopciontfg.app.ui.screens.components.AppSecondaryButton
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onContinueClick: (email: String, password: String) -> Unit,
    onShelterPreviewClick: () -> Unit,
) {
    var user by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    var showEmptyFieldsError by rememberSaveable { mutableStateOf(false) }
    val email = user.trim()
    val isEmailInvalid = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopAppBar(
                title = stringResource(R.string.login),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
            ) {
                Text(
                    text = stringResource(R.string.login_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = Dimens.spacingSm)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    if (isEmailInvalid) {
                        Text(
                            text = stringResource(R.string.login_invalid_email_error),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    AppFilledTextField(
                        value = user,
                        onValueChange = {
                            user = it
                            if (it.isNotBlank() && password.isNotBlank()) {
                                showEmptyFieldsError = false
                            }
                        },
                        label = { Text(stringResource(R.string.correo)) },
                        isError = isEmailInvalid,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                AppFilledTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (user.isNotBlank() && it.isNotBlank()) {
                            showEmptyFieldsError = false
                        }
                    },
                    label = { Text(stringResource(R.string.contraseña)) },
                    visualTransformation = if (passwordHidden) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordHidden = !passwordHidden }) {
                            Icon(
                                imageVector = if (passwordHidden) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )

                if (showEmptyFieldsError) {
                    Text(
                        text = stringResource(R.string.login_empty_fields_error),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }

            AppPrimaryButton(
                text = stringResource(R.string.continuar),
                onClick = {
                    when {
                        email.isBlank() || password.isBlank() -> {
                            showEmptyFieldsError = true
                        }

                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            showEmptyFieldsError = false
                        }

                        else -> {
                            showEmptyFieldsError = false
                            onContinueClick(email, password)
                        }
                    }
                },
                modifier = Modifier.padding(top = Dimens.spacingLg)
            )
            AppSecondaryButton(
                text = stringResource(R.string.ver_pantallas_protectora),
                onClick = onShelterPreviewClick,
                modifier = Modifier.padding(top = Dimens.spacingSm)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AdoptionTheme {
        LoginScreen(onBackClick = {}, onContinueClick = { _, _ -> }, onShelterPreviewClick = {})
    }
}
