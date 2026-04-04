package com.example.adopciontfg.app.ui.screens.shelter_registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lint.kotlin.metadata.Visibility
import com.example.adopciontfg.app.ui.screens.components.SavePhotos

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ShelterRegistration() {

    var name by rememberSaveable { mutableStateOf("") }
    var cif by rememberSaveable { mutableStateOf("") }
    var telf by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "REGISTRO DE PROTECTORA") },
                navigationIcon = {
                    IconButton(onClick = {}) {
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
                label = { Text("NOMBRE") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = cif,
                onValueChange = { cif = it },
                singleLine = true,
                label = { Text("CIF") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = telf,
                onValueChange = { telf = it },
                singleLine = true,
                label = { Text("TELÉFONO") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = address,
                onValueChange = { address = it },
                singleLine = true,
                placeholder = {Text("Calle, número, ciudad")},
                label = { Text("DIRECCIÓN") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                label = { Text("CORREO") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                label = { Text("CONTRASEÑA") },
                visualTransformation = if (passwordHidden)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val icon =
                            if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description =
                            if (passwordHidden) "Show password" else "Hide password"

                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )

            Text("SELECCIONE FOTO DE PERFIL")

            //componente comun a la pantalla de registro de usuario y animal
            SavePhotos()

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Registrar")
            }
        }
    }
}