package com.example.adopciontfg.app.ui.screens.user_home_screen.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.adopciontfg.data.local.entity.ShelterEntity
import com.example.adopciontfg.data.util.geocodeAddress
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private const val GoogleMapsPackage = "com.google.android.apps.maps"

private data class ShelterMapMarker(
    val shelter: ShelterEntity,
    val position: GeoPoint,
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
    val madrid = GeoPoint(40.4168, -3.7038)
    var selectedShelter by remember { mutableStateOf<ShelterEntity?>(null) }
    var mapMarkers by remember { mutableStateOf<List<ShelterMapMarker>>(emptyList()) }
    val context = LocalContext.current

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
                }
            },
            update = { mapView ->
                mapView.controller.setZoom(12.5)
                mapView.controller.setCenter(madrid)

                mapView.overlays.clear()

                mapMarkers.forEach { markerData ->
                    val marker = Marker(mapView).apply {
                        position = markerData.position
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = markerData.shelter.name

                        setOnMarkerClickListener { _, _ ->
                            mapView.controller.animateTo(markerData.position)
                            selectedShelter = markerData.shelter
                            true
                        }
                    }

                    mapView.overlays.add(marker)
                }

                mapView.invalidate()
            }
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
