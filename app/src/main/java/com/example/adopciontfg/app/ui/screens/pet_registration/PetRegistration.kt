package com.example.adopciontfg.app.ui.screens.pet_registration


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.app.ui.screens.components.SavePhotos

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ShelterRegistration() {

    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    val male  = remember { mutableStateOf(false) }
    val female  = remember { mutableStateOf(false) }
    var characteristics by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "REGISTRO ANIMAL") },
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
                modifier = Modifier
                    .fillMaxWidth()
            )

            TextField(
                value = age,
                onValueChange = { age = it },
                singleLine = true,
                label = { Text("EDAD") },
                modifier = Modifier
                    .fillMaxWidth()

            )
            //cambiar a radiobutton
            Row{
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Checkbox(
                        checked = male.value,
                        onCheckedChange = { male.value = it }
                    )
                    Text("Macho")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = female.value,
                        onCheckedChange = {female.value = it}
                    )
                    Text("Hembra")
                }
            }
            TextField(
                value = description,
                onValueChange = { description = it },
                singleLine = true,
                label = { Text("DESCRIPCION") },
                modifier = Modifier
                    .fillMaxWidth()

            )
            Spacer(modifier = Modifier.padding(10.dp))

            Text("ADJUNTAR FOTOS")

            //componente comun a la pantalla de registro de usuario, animal y protectora
            SavePhotos()

            TextField(
                value = characteristics,
                onValueChange = { characteristics = it },
                singleLine = true,
                label = { Text("CARACTERISTICAS") },
                modifier = Modifier
                    .fillMaxWidth()

            )
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Registrar animal ")
            }
        }
    }
}