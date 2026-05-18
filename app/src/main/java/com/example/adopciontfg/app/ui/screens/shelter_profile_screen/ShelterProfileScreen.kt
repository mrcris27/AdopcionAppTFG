package com.example.adopciontfg.app.ui.screens.shelter_profile_screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.app.ui.components.skeleton.ShelterCardListSkeleton
import com.example.adopciontfg.app.ui.screens.components.AppSectionTitle
import com.example.adopciontfg.app.ui.screens.components.AppTopAppBar
import com.example.adopciontfg.app.ui.screens.components.CardViewList
import com.example.adopciontfg.app.ui.screens.components.ListCardView
import com.example.adopciontfg.data.local.entity.AnimalEntity
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.local.entity.listSubtitle
import com.example.adopciontfg.data.sampleAnimals
import com.example.adopciontfg.data.sampleShelters
import com.example.adopciontfg.ui.theme.AdoptionTheme
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface
import com.example.adopciontfg.ui.theme.subtleDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterProfileScreen(
    shelter: ShelterEntity,
    animals: List<AnimalEntity>,
    onBackClick: () -> Unit,
    onPetClick: (String) -> Unit,
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
            AppTopAppBar(
                title = "Protectora",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ElevatedCard(
                onClick = { shelterCardExpanded = !shelterCardExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm)
                    .animateContentSize(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.elevatedSurface()
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.cardPadding)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(Dimens.avatarSize),
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
                                    modifier = Modifier.size(44.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(Dimens.spacingMd))

                        Text(
                            text = shelter.name.orEmpty(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
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
                        Spacer(modifier = Modifier.height(Dimens.spacingMd))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.subtleDivider()
                        )
                        Spacer(modifier = Modifier.height(Dimens.spacingMd))

                        val hasContactDetails = shelter.address.orEmpty().isNotBlank() ||
                            shelter.cif.orEmpty().isNotBlank() ||
                            shelter.email.orEmpty().isNotBlank() ||
                            shelter.phone.orEmpty().isNotBlank()

                        if (!hasContactDetails) {
                            Text(
                                text = "Aquí se mostrará la información de la protectora cuando esté disponible.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            if (shelter.address.orEmpty().isNotBlank()) {
                                Text(
                                    text = shelter.address.orEmpty(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (shelter.cif.orEmpty().isNotBlank()) {
                                Spacer(modifier = Modifier.height(Dimens.spacingSm))
                                Text(
                                    text = "CIF: ${shelter.cif.orEmpty()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (shelter.email.orEmpty().isNotBlank()) {
                                Spacer(modifier = Modifier.height(Dimens.spacingSm))
                                Text(
                                    text = shelter.email.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (shelter.phone.orEmpty().isNotBlank()) {
                                Spacer(modifier = Modifier.height(Dimens.spacingSm))
                                Text(
                                    text = shelter.phone.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            AppSectionTitle(
                text = "Animales disponibles",
                modifier = Modifier.padding(
                    horizontal = Dimens.spacingLg,
                    vertical = Dimens.spacingSm
                )
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isPetsLoading) {
                    ShelterCardListSkeleton(modifier = Modifier.fillMaxSize())
                } else if (animals.isEmpty()) {
                    Text(
                        text = "No hay animales disponibles en esta protectora.",
                        modifier = Modifier.padding(horizontal = Dimens.spacingLg),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    ListCardView(
                        items = animals,
                        onItemClick = { animal -> onPetClick(animal.id) },
                        itemContent = { animal, onClick ->
                            CardViewList(
                                name = animal.name.orEmpty(),
                                subtitle = animal.listSubtitle(),
                                onClick = onClick,
                            )
                        },
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
            shelter = sampleShelters.first(),
            animals = sampleAnimals.filter { it.shelterId == "1" },
            onBackClick = {},
            onPetClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Lista animales (cargando)")
@Composable
fun ShelterProfilePetsLoadingPreview() {
    AdoptionTheme {
        ShelterProfileScreen(
            shelter = sampleShelters.first(),
            animals = emptyList(),
            onBackClick = {},
            onPetClick = {},
            isPetsLoading = true,
        )
    }
}
