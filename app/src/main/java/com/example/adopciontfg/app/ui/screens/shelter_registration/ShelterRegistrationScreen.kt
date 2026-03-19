package com.example.adopciontfg.app.ui.screens.shelter_registration

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

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



    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(text = "REGISTRO DE PROTECTORA")
                }
            )
        }
    ) {innerPadding->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item{
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {Text("NOMBRE")}
                )
                TextField(
                    value = cif,
                    onValueChange = { cif = it },
                    label = {Text("CIF")}
                )
                TextField(
                    value = telf,
                    onValueChange = { telf = it },
                    label = {Text("TELEFONO")}
                )
                TextField(
                    value = address,
                    onValueChange = { address = it },
                    label = {Text("DIRECCIÓN")}
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {Text("CORREO")}
                )

                //investigar como añadir foto del dispositivo
            }
        }
    }
}