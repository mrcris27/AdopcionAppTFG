package com.example.adopciontfg.app.ui.screens.user.home.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.adopciontfg.R
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.util.geocodeAddress
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.min
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

private const val GoogleMapsPackage = "com.google.android.apps.maps"
private const val InitialZoom = 5.6
private const val DetailZoom = 13.0
private const val ClusterZoomLimit = 10.5

private val SpainCenter = GeoPoint(40.4168, -3.7038)
private val SpainBounds = BoundingBox(
    44.3,
    4.6,
    27.5,
    -18.5
)

private data class ShelterMapMarker(
    val shelter: ShelterEntity,
    val position: GeoPoint,
)

private data class ShelterMapCluster(
    val center: GeoPoint,
    val markers: List<ShelterMapMarker>,
)

private data class DrawnCluster(
    val cluster: ShelterMapCluster,
    val screenPoint: Point,
    val radius: Float,
)

/**
 * Abre la ubicación en la app Google Maps si está instalada; si no, en el navegador (Google Maps web).
 */
private fun openShelterInGoogleMaps(context: Context, shelter: ShelterEntity) {
    val address = shelter.address?.trim().orEmpty()
    if (address.isEmpty()) return

    val uri = Uri.parse(
        "https://www.google.com/maps/search/?api=1&query=${Uri.encode(address)}"
    )
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage(GoogleMapsPackage)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        intent.setPackage(null)
        context.startActivity(intent)
    }
}

@Composable
fun ShelterMapView(
    shelters: List<ShelterEntity>,
    modifier: Modifier = Modifier
) {
    var selectedShelter by remember { mutableStateOf<ShelterEntity?>(null) }
    var mapMarkers by remember { mutableStateOf<List<ShelterMapMarker>>(emptyList()) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var mapQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val visibleMarkers = remember(mapMarkers, mapQuery) {
        val query = mapQuery.trim()
        if (query.isEmpty()) {
            mapMarkers
        } else {
            mapMarkers.filter { marker ->
                marker.shelter.name.orEmpty().contains(query, ignoreCase = true) ||
                    marker.shelter.address.orEmpty().contains(query, ignoreCase = true)
            }
        }
    }
    val searchResults = remember(visibleMarkers, mapQuery) {
        if (mapQuery.isBlank()) emptyList() else visibleMarkers.take(4)
    }

    LaunchedEffect(shelters) {
        mapMarkers = shelters.mapNotNull { shelter ->
            val address = shelter.address?.trim().orEmpty()
            if (address.isEmpty()) return@mapNotNull null
            val position = geocodeAddress(context, address) ?: return@mapNotNull null
            ShelterMapMarker(shelter, position)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            factory = { ctx ->
                MapView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    clipToPadding = true
                    clipChildren = true
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                    minZoomLevel = InitialZoom
                    maxZoomLevel = 18.0
                    setScrollableAreaLimitDouble(SpainBounds)
                    controller.setZoom(InitialZoom)
                    controller.setCenter(SpainCenter)
                    mapViewRef = this
                }
            },
            update = { mapView ->
                mapView.overlays.clear()
                mapView.overlays.add(
                    ShelterClusterOverlay(
                        markers = visibleMarkers,
                        onShelterClick = { shelter, position ->
                            mapView.controller.animateTo(position)
                            selectedShelter = shelter
                        },
                        onClusterClick = { center ->
                            mapView.controller.animateTo(center)
                            mapView.controller.zoomTo(min(mapView.zoomLevelDouble + 2.0, DetailZoom))
                            selectedShelter = null
                        }
                    )
                )

                mapView.invalidate()
            }
        )

        MapSearchPanel(
            query = mapQuery,
            isExpanded = isSearchExpanded,
            results = searchResults,
            onQueryChange = { mapQuery = it },
            onClear = {
                mapQuery = ""
                selectedShelter = null
            },
            onExpand = { isSearchExpanded = true },
            onCollapse = { isSearchExpanded = false },
            onShelterSelected = { marker ->
                mapQuery = marker.shelter.name.orEmpty()
                isSearchExpanded = false
                selectedShelter = marker.shelter
                mapViewRef?.controller?.apply {
                    setZoom(DetailZoom)
                    animateTo(marker.position)
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        MapControls(
            onZoomIn = {
                mapViewRef?.controller?.zoomTo(
                    min((mapViewRef?.zoomLevelDouble ?: InitialZoom) + 1.0, 18.0)
                )
            },
            onZoomOut = {
                mapViewRef?.controller?.zoomTo(
                    maxOf((mapViewRef?.zoomLevelDouble ?: InitialZoom) - 1.0, InitialZoom)
                )
            },
            onCenterSpain = {
                selectedShelter = null
                mapViewRef?.controller?.apply {
                    setZoom(InitialZoom)
                    animateTo(SpainCenter)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )

        AnimatedVisibility(
            visible = selectedShelter != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            selectedShelter?.let { shelter ->
                MapCard(
                    shelter = shelter,
                    onClick = { openShelterInGoogleMaps(context, shelter) }
                )
            }
        }
    }
}

@Composable
private fun MapSearchPanel(
    query: String,
    isExpanded: Boolean,
    results: List<ShelterMapMarker>,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    onShelterSelected: (ShelterMapMarker) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isExpanded) {
        ElevatedCard(
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            IconButton(onClick = onExpand) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.buscar_protectora)
                )
            }
        }
        return
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text(stringResource(R.string.buscar)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = onClear) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.limpiar_busqueda)
                                )
                            }
                        }
                    }
                )

                IconButton(onClick = onCollapse) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.plegar_buscador)
                    )
                }
            }

            results.forEach { marker ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShelterSelected(marker) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = marker.shelter.name.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = marker.shelter.address.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MapControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onCenterSpain: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextButton(onClick = onZoomIn) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
            HorizontalDivider(modifier = Modifier.width(44.dp))
            TextButton(onClick = onZoomOut) {
                Text("-", style = MaterialTheme.typography.titleLarge)
            }
            HorizontalDivider(modifier = Modifier.width(44.dp))
            TextButton(onClick = onCenterSpain) {
                Text(stringResource(R.string.mapa_centrar_espana_abreviado), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private class ShelterClusterOverlay(
    private val markers: List<ShelterMapMarker>,
    private val onShelterClick: (ShelterEntity, GeoPoint) -> Unit,
    private val onClusterClick: (GeoPoint) -> Unit,
) : Overlay() {
    private val clusterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(46, 125, 50)
        style = Paint.Style.FILL
    }
    private val clusterStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val clusterTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 34f
        typeface = Typeface.DEFAULT_BOLD
    }
    private val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(46, 125, 50)
        style = Paint.Style.FILL
    }
    private val markerStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }
    private val markerInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val textBounds = Rect()
    private val markerPath = Path()
    private var drawnClusters: List<DrawnCluster> = emptyList()

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        if (shadow) return

        drawnClusters = buildClusters(mapView).map { cluster ->
            val point = mapView.projection.toPixels(cluster.center, null)
            val radius = if (cluster.markers.size > 1) {
                30f + min(cluster.markers.size * 3f, 18f)
            } else {
                48f
            }

            if (cluster.markers.size > 1) {
                canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), radius, clusterPaint)
                canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), radius, clusterStrokePaint)
                drawCenteredText(canvas, cluster.markers.size.toString(), point)
            } else {
                drawMapPin(canvas, point)
            }

            DrawnCluster(cluster, point, radius)
        }
    }

    override fun onSingleTapConfirmed(event: MotionEvent, mapView: MapView): Boolean {
        val tappedCluster = drawnClusters
            .asReversed()
            .firstOrNull { drawn ->
                val distance = hypot(
                    abs(event.x - drawn.screenPoint.x),
                    abs(event.y - drawn.screenPoint.y)
                )
                distance <= drawn.radius + 16f
            } ?: return false

        if (tappedCluster.cluster.markers.size > 1) {
            onClusterClick(tappedCluster.cluster.center)
        } else {
            val marker = tappedCluster.cluster.markers.first()
            onShelterClick(marker.shelter, marker.position)
        }
        return true
    }

    private fun buildClusters(mapView: MapView): List<ShelterMapCluster> {
        if (markers.isEmpty()) return emptyList()
        if (mapView.zoomLevelDouble >= ClusterZoomLimit) {
            return markers.map { ShelterMapCluster(it.position, listOf(it)) }
        }

        val cellSize = clusterCellSize(mapView.zoomLevelDouble)
        val buckets = linkedMapOf<Pair<Int, Int>, MutableList<ShelterMapMarker>>()

        markers.forEach { marker ->
            val point = mapView.projection.toPixels(marker.position, null)
            val key = Pair(point.x / cellSize, point.y / cellSize)
            buckets.getOrPut(key) { mutableListOf() }.add(marker)
        }

        return buckets.values.map { bucket ->
            val latitude = bucket.sumOf { it.position.latitude } / bucket.size
            val longitude = bucket.sumOf { it.position.longitude } / bucket.size
            ShelterMapCluster(
                center = GeoPoint(latitude, longitude),
                markers = bucket
            )
        }
    }

    private fun clusterCellSize(zoom: Double): Int = when {
        zoom < 6.8 -> 190
        zoom < 8.5 -> 150
        else -> 115
    }

    private fun drawCenteredText(canvas: Canvas, text: String, point: Point) {
        clusterTextPaint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(
            text,
            point.x.toFloat(),
            point.y - textBounds.exactCenterY(),
            clusterTextPaint
        )
    }

    private fun drawMapPin(canvas: Canvas, point: Point) {
        val x = point.x.toFloat()
        val y = point.y.toFloat()
        val headCenterY = y - 43f

        markerPath.reset()
        markerPath.moveTo(x, y)
        markerPath.cubicTo(x - 34f, y - 30f, x - 30f, y - 78f, x, y - 86f)
        markerPath.cubicTo(x + 30f, y - 78f, x + 34f, y - 30f, x, y)
        markerPath.close()

        canvas.drawPath(markerPath, markerPaint)
        canvas.drawPath(markerPath, markerStrokePaint)
        canvas.drawCircle(x, headCenterY, 11f, markerInnerPaint)
    }
}
