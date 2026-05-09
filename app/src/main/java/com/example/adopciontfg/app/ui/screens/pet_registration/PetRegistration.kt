package com.example.adopciontfg.app.ui.screens.pet_registration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.app.ui.screens.components.SavePhotos
import com.example.adopciontfg.ui.theme.AdoptionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetRegistration() {

    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var male by remember { mutableStateOf(false) }
    var female by remember { mutableStateOf(false) }
    var characteristics by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = { Text("REGISTRO ANIMAL") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("NOMBRE") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("EDAD") },
                modifier = Modifier.fillMaxWidth()
            )

            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = male, onCheckedChange = { male = it })
                    Text("Macho")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = female, onCheckedChange = { female = it })
                    Text("Hembra")
                }
            }

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("DESCRIPCIÓN") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("ADJUNTAR FOTOS")

            SavePhotos()

            TextField(
                value = characteristics,
                onValueChange = { characteristics = it },
                label = { Text("CARACTERÍSTICAS") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Registrar animal")
            }
        }
    }
}

@Preview
@Composable
fun PetRegistrationPreview() {
    AdoptionTheme {
        PetRegistration()
    }
}