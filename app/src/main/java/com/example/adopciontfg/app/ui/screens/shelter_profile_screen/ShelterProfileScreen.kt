package com.example.adopciontfg.app.ui.screens.shelter_profile_screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.app.ui.components.skeleton.ShelterCardListSkeleton
import com.example.adopciontfg.app.ui.screens.components.CardViewList
import com.example.adopciontfg.app.ui.screens.components.ListCardView
import com.example.adopciontfg.data.Shelter
import com.example.adopciontfg.ui.theme.AdoptionTheme

private data class AdoptionPetRow(
    val id: String,
    val displayName: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterProfileScreen(
    shelter: Shelter,
    onBackClick: () -> Unit,
    onPetClick: (String) -> Unit,
    /** Cuando los animales se cargan de red o base de datos, mostrar skeleton en la lista. */
    isPetsLoading: Boolean = false,
) {
    var shelterCardExpanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (shelterCardExpanded) 180f else 0f,
        label = "shelterCardChevron",
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PROTECTORA",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            ElevatedCard(
                onClick = { shelterCardExpanded = !shelterCardExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = shelter.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = if (shelterCardExpanded) {
                                "Ocultar información de la protectora"
                            } else {
                                "Mostrar información de la protectora"
                            },
                            modifier = Modifier.rotate(chevronRotation),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (shelterCardExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        Spacer(modifier = Modifier.height(10.dp))

                        val hasContactDetails = shelter.address.isNotBlank() ||
                            shelter.cif.isNotBlank() ||
                            shelter.email.isNotBlank() ||
                            shelter.phone.isNotBlank()

                        if (!hasContactDetails) {
                            Text(
                                text = "Aquí se mostrará la información de la protectora cuando esté disponible.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            if (shelter.address.isNotBlank()) {
                                Text(
                                    text = shelter.address,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (shelter.cif.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "CIF: ${shelter.cif}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (shelter.email.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = shelter.email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (shelter.phone.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = shelter.phone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Animales disponibles",
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            val animals = listOf(
                AdoptionPetRow("1", "Animal 1"),
                AdoptionPetRow("2", "Animal 2"),
                AdoptionPetRow("3", "Animal 3"),
                AdoptionPetRow("4", "Animal 4"),
                AdoptionPetRow("5", "Animal 5"),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isPetsLoading) {
                    ShelterCardListSkeleton(modifier = Modifier.fillMaxSize())
                } else {
                    ListCardView(
                        items = animals,
                        onItemClick = { row -> onPetClick(row.id) },
                        itemContent = { row, onClick ->
                            CardViewList(
                                name = row.displayName,
                                onClick = onClick
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShelterProfileViewPreview() {
    AdoptionTheme {
        ShelterProfileScreen(
            shelter = Shelter(
                id = "1",
                name = "Protectora Ejemplo",
                lat = 0.0,
                lng = 0.0,
                cif = "A12345678",
                profilePicture = "",
                email = "contacto@protectora.com",
                address = "Calle Ejemplo 123",
                phone = "+34 123 456 789"
            ),
            onBackClick = {},
            onPetClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Lista animales (cargando)")
@Composable
fun ShelterProfilePetsLoadingPreview() {
    AdoptionTheme {
        ShelterProfileScreen(
            shelter = Shelter(
                id = "1",
                name = "Protectora Ejemplo",
                lat = 0.0,
                lng = 0.0,
                cif = "A12345678",
                profilePicture = "",
                email = "contacto@protectora.com",
                address = "Calle Ejemplo 123",
                phone = "+34 123 456 789"
            ),
            onBackClick = {},
            onPetClick = {},
            isPetsLoading = true
        )
    }
}