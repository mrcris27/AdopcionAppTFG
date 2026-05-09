package com.example.adopciontfg.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.ui.theme.AdoptionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsHubScreen(
    onUserSettingsClick: () -> Unit,
    onShelterSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Selecciona el tipo de perfil para editar sus preferencias.",
                style = MaterialTheme.typography.bodyMedium
            )

            SettingsProfileCard(
                title = "Ajustes de usuario",
                subtitle = "Perfil, contacto y preferencias de la app",
                icon = { androidx.compose.material3.Icon(Icons.Outlined.Person, contentDescription = null) },
                onClick = onUserSettingsClick
            )

            SettingsProfileCard(
                title = "Ajustes de protectora",
                subtitle = "Datos de la entidad y alertas de adopcion",
                icon = { androidx.compose.material3.Icon(Icons.Outlined.VolunteerActivism, contentDescription = null) },
                onClick = onShelterSettingsClick
            )
        }
    }
}

@Composable
private fun SettingsProfileCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon()
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsHubScreenPreview() {
    AdoptionTheme {
        SettingsHubScreen(
            onUserSettingsClick = {},
            onShelterSettingsClick = {}
        )
    }
}
