package com.example.adopciontfg.app.ui.screens.user_registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.R
import com.example.adopciontfg.ui.theme.AdoptionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegistration(
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {

    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password1 by rememberSaveable { mutableStateOf("") }
    var passwordHidden1 by rememberSaveable { mutableStateOf(true) }
    var password2 by rememberSaveable { mutableStateOf("") }
    var passwordHidden2 by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title ={ Text(
                    text = stringResource(R.string.registro_usuario),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp) // margen general bonito
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text(stringResource(R.string.nombre) ) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextField(
                value = surname,
                onValueChange = { surname = it },
                singleLine = true,
                label = { Text(stringResource(R.string.apellidos)) },
                modifier = Modifier
                    .fillMaxWidth()
            )

            TextField(
                value = email,
                onValueChange = {email = it},
                label = { Text(stringResource(R.string.correo))},
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = password1,
                onValueChange = { password1 = it },
                singleLine = true,
                label = { Text(stringResource(R.string.contraseña) ) },
                visualTransformation = if (passwordHidden1)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { passwordHidden1 = !passwordHidden1 }) {
                        val icon =
                            if (passwordHidden1) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description =
                            if (passwordHidden1) "Show password" else "Hide password"

                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )
            TextField(
                value = password2,
                onValueChange = { password2 = it },
                singleLine = true,
                label = { Text(stringResource(R.string.contraseña2) ) },
                visualTransformation = if (passwordHidden2)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { passwordHidden2 = !passwordHidden2 }) {
                        val icon =
                            if (passwordHidden2) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description =
                            if (passwordHidden2) "Show password" else "Hide password"

                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.continuar))
            }

        }

    }

}

@Preview(showBackground = true)
@Composable
fun UserRegistrationPreview() {
    AdoptionTheme {
        UserRegistration(onBackClick = {}, onRegisterClick = {})
    }
}