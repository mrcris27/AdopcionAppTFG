package com.example.adopciontfg.app.ui.screens.pet_detail_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.app.ui.components.skeleton.PetDetailBottomBarSkeleton
import com.example.adopciontfg.app.ui.components.skeleton.PetDetailContentSkeleton
import com.example.adopciontfg.app.ui.components.skeleton.PetDetailTopBarTitleSkeleton
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.local.entity.ageInYears
import com.example.adopciontfg.data.local.entity.displayPhotos
import com.example.adopciontfg.data.local.entity.formattedCharacteristics
import com.example.adopciontfg.data.local.entity.genderLabel
import com.example.adopciontfg.model.Characteristic
import com.example.adopciontfg.model.Species
import com.example.adopciontfg.ui.theme.AdoptionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    animal: AnimalEntity?,
    onBackClick: () -> Unit,
    onAdoptClick: () -> Unit,
    isLoading: Boolean = false,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isLoading) {
                        PetDetailTopBarTitleSkeleton()
                    } else {
                        Text(animal?.name.orEmpty())
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (isLoading) {
                PetDetailBottomBarSkeleton()
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onAdoptClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Adoptar ahora")
                    }
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Donar")
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading || animal == null) {
            PetDetailContentSkeleton(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        } else {
            val photos = animal.displayPhotos()
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoRow("Nombre", animal.name.orEmpty())
                            InfoRow("Edad", "${animal.ageInYears()} años")
                            InfoRow("Especie", animal.species?.name.orEmpty())
                            InfoRow("Género", animal.genderLabel())
                            if (!animal.characteristics.isNullOrEmpty()) {
                                InfoRow("Características", animal.formattedCharacteristics())
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sobre esta mascota",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = animal.description?.takeIf { it.isNotBlank() }
                                    ?: "Esta mascota busca un hogar lleno de amor. " +
                                    "Está esperando a alguien especial que le dé una segunda oportunidad 🐾",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    PetPhotosCarousel(photos)
                }
            }
        }
    }
}

@Composable
private fun PetPhotosCarousel(photos: List<String>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Fotos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin fotos disponibles",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                itemsIndexed(photos, key = { index, _ -> index }) { _, _ ->
                    Box(
                        modifier = Modifier
                            .width(280.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🐶",
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun previewAnimal(): AnimalEntity = AnimalEntity(
    "1",
    "Max",
    false,
    "",
    listOf("", "", ""),
    1672531200000L,
    "Max es un perro muy cariñoso y juguetón. Le encanta pasear y jugar con la pelota.",
    Species.PERRO,
    listOf(Characteristic.SOCIABLE_CON_PERROS, Characteristic.JUGUETON),
    "1",
)

@Preview(showBackground = true, name = "Cargando")
@Composable
fun PetDetailLoadingPreview() {
    AdoptionTheme {
        PetDetailScreen(
            animal = null,
            onBackClick = {},
            onAdoptClick = {},
            isLoading = true,
        )
    }
}

@Preview
@Composable
fun PetDetailPreview() {
    AdoptionTheme {
        PetDetailScreen(
            animal = previewAnimal(),
            onBackClick = {},
            onAdoptClick = {},
        )
    }
}
