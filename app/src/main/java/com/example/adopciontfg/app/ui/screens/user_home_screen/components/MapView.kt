package com.example.adopciontfg.app.ui.screens.user_home_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.adopciontfg.data.Shelter
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun ShelterMapView(
    shelters: List<Shelter>
) {
    val madrid = GeoPoint(40.4168, -3.7038)

    //añadir una x en la card para salirse
    // estado del marcador seleccionado
    var selectedShelter by remember { mutableStateOf<Shelter?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                }
            },
            update = { mapView ->

                mapView.controller.setZoom(12.5)
                mapView.controller.setCenter(madrid)

                mapView.overlays.clear()

                shelters.forEach { shelter ->

                    val marker = Marker(mapView).apply {
                        position = GeoPoint(shelter.lat, shelter.lng)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = shelter.name

                        setOnMarkerClickListener { _, _ ->
                            mapView.controller.animateTo(GeoPoint(shelter.lat, shelter.lng))
                            selectedShelter = shelter
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
                    onClick = {
                        // aquí navegarás a detalle
                    }
                )
            }
        }
    }
}