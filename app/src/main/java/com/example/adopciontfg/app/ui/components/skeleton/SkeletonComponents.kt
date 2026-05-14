package com.example.adopciontfg.app.ui.components.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val SHIMMER_DURATION_MS = 1100

@Composable
fun rememberShimmerBrush(): Brush {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val colors = listOf(
        base.copy(alpha = 0.38f),
        base.copy(alpha = 0.92f),
        base.copy(alpha = 0.38f),
    )
    val transition = rememberInfiniteTransition(label = "skeleton_shimmer")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton_shimmer_shift"
    )
    return Brush.linearGradient(
        colors = colors,
        start = Offset(shift - 400f, shift - 400f),
        end = Offset(shift, shift)
    )
}

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

/**
 * Fila tipo [com.example.adopciontfg.app.ui.screens.components.CardViewList]: imagen + título + subtítulo.
 */
@Composable
fun ShelterCardRowSkeleton(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier.size(70.dp),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(18.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.48f)
                        .height(12.dp)
                )
            }
        }
    }
}

@Composable
fun ShelterCardListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = contentPadding,
    ) {
        items(itemCount) {
            ShelterCardRowSkeleton()
        }
    }
}

@Composable
fun MapAreaSkeleton(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(rememberShimmerBrush())
    )
}

/**
 * Cabecera tipo tarjeta de protectora en [com.example.adopciontfg.app.ui.screens.shelter_profile_screen.ShelterProfileScreen].
 */
@Composable
fun ShelterProfileHeaderCardSkeleton(
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier.size(48.dp),
                shape = CircleShape
            )
            Spacer(modifier = Modifier.width(12.dp))
            SkeletonBox(
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            SkeletonBox(
                modifier = Modifier.size(24.dp),
                shape = CircleShape
            )
        }
    }
}

@Composable
fun ShelterProfilePetsSectionSkeleton(
    modifier: Modifier = Modifier,
    listItemCount: Int = 5,
) {
    Column(modifier = modifier) {
        SkeletonBox(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(0.45f)
                .height(22.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        ShelterCardListSkeleton(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            itemCount = listItemCount
        )
    }
}

/**
 * Contenido de perfil completo en carga (cabecera + lista de animales).
 */
@Composable
fun ShelterProfileScreenSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        ShelterProfileHeaderCardSkeleton()
        Spacer(modifier = Modifier.height(14.dp))
        ShelterProfilePetsSectionSkeleton(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .fillMaxHeight(),
            listItemCount = 6
        )
    }
}

@Composable
fun PetDetailTopBarTitleSkeleton(
    modifier: Modifier = Modifier,
) {
    SkeletonBox(
        modifier = modifier
            .width(152.dp)
            .height(22.dp),
        shape = RoundedCornerShape(6.dp)
    )
}

@Composable
fun PetDetailBottomBarSkeleton(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonBox(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp)
        )
        SkeletonBox(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun PetDetailInfoRowSkeleton(
    valueWidth: Dp,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonBox(
            modifier = Modifier
                .width(76.dp)
                .height(14.dp)
        )
        SkeletonBox(
            modifier = Modifier
                .width(valueWidth)
                .height(14.dp)
        )
    }
}

@Composable
fun PetDetailInfoCardSkeleton(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        val valueWidths = listOf(118.dp, 92.dp, 132.dp, 104.dp, 88.dp)
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            valueWidths.forEach { w ->
                PetDetailInfoRowSkeleton(valueWidth = w)
            }
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(0.48f)
                    .height(18.dp)
            )
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(12.dp)
            )
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(12.dp)
            )
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(12.dp)
            )
        }
    }
}

@Composable
fun PetDetailPhotosSectionSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth(0.22f)
                .height(18.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(3) {
                SkeletonBox(
                    modifier = Modifier
                        .width(280.dp)
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

/**
 * Cuerpo scrollable del detalle de mascota ([com.example.adopciontfg.app.ui.screens.pet_detail_screen.PetDetailScreen]) en carga.
 */
@Composable
fun PetDetailContentSkeleton(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item { PetDetailInfoCardSkeleton() }
        item { PetDetailPhotosSectionSkeleton() }
    }
}
