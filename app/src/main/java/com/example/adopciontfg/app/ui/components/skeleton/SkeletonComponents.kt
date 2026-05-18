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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import com.example.adopciontfg.ui.theme.Dimens
import com.example.adopciontfg.ui.theme.elevatedSurface
import com.example.adopciontfg.ui.theme.subtleDivider

private const val SHIMMER_DURATION_MS = 1100

@Composable
fun rememberShimmerBrush(): Brush {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val colors = listOf(
        base.copy(alpha = 0.35f),
        base.copy(alpha = 0.85f),
        base.copy(alpha = 0.35f),
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
    shape: Shape = MaterialTheme.shapes.small,
) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

/** Fila alineada con [CardViewList]: thumbnail + textos + chevron. */
@Composable
fun ShelterCardRowSkeleton(
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .padding(horizontal = Dimens.spacingLg)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.elevatedSurface()
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier.size(Dimens.thumbnailSize),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.width(Dimens.spacingLg))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
            ) {
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                )
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(12.dp)
                )
            }
            SkeletonBox(
                modifier = Modifier.size(24.dp),
                shape = MaterialTheme.shapes.small
            )
        }
    }
}

@Composable
fun ShelterCardListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6,
    contentPadding: PaddingValues = PaddingValues(vertical = Dimens.spacingSm),
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.listItemSpacing),
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.spacingSm),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(rememberShimmerBrush())
        )
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.elevatedSurface()
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.cardPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(
                    modifier = Modifier.size(44.dp),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.width(Dimens.spacingMd))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(14.dp)
                    )
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth(0.35f)
                            .height(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ShelterProfileHeaderCardSkeleton(
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.elevatedSurface()
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier.size(Dimens.avatarSize),
                shape = CircleShape
            )
            Spacer(modifier = Modifier.width(Dimens.spacingMd))
            SkeletonBox(
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp)
            )
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
                .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm)
                .fillMaxWidth(0.4f)
                .height(16.dp)
        )
        ShelterCardListSkeleton(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            itemCount = listItemCount
        )
    }
}

@Composable
fun ShelterProfileScreenSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ShelterProfileHeaderCardSkeleton()
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
        shape = MaterialTheme.shapes.small
    )
}

@Composable
fun PetDetailBottomBarSkeleton(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.cardPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        SkeletonBox(
            modifier = Modifier
                .weight(1f)
                .height(Dimens.buttonHeight),
            shape = MaterialTheme.shapes.large
        )
        SkeletonBox(
            modifier = Modifier
                .weight(1f)
                .height(Dimens.buttonHeight),
            shape = MaterialTheme.shapes.large
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
            containerColor = MaterialTheme.colorScheme.elevatedSurface()
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        val valueWidths = listOf(118.dp, 92.dp, 132.dp, 104.dp, 88.dp)
        Column(
            modifier = Modifier.padding(Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            valueWidths.forEachIndexed { index, w ->
                PetDetailInfoRowSkeleton(valueWidth = w)
                if (index == 0) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.subtleDivider()
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.spacingXs))
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(0.42f)
                    .height(14.dp)
            )
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(12.dp)
            )
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
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
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth(0.2f)
                .height(18.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            contentPadding = PaddingValues(vertical = Dimens.spacingXs)
        ) {
            items(3) {
                SkeletonBox(
                    modifier = Modifier
                        .width(280.dp)
                        .height(200.dp),
                    shape = MaterialTheme.shapes.large
                )
            }
        }
    }
}

@Composable
fun PetDetailContentSkeleton(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
        contentPadding = PaddingValues(Dimens.screenPadding)
    ) {
        item { PetDetailInfoCardSkeleton() }
        item { PetDetailPhotosSectionSkeleton() }
    }
}
