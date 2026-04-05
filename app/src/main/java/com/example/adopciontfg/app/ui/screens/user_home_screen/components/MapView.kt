package com.example.adopciontfg.app.ui.screens.user_home_screen.components

import androidx.compose.runtime.Composable
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

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
            }
        },
        update = { mapView ->

            // centrar mapa
            mapView.controller.setZoom(12.5)
            mapView.controller.setCenter(madrid)

            // limpiar markers antiguos (IMPORTANTE)
            mapView.overlays.clear()

            // añadir markers
            shelters.forEach { shelter ->

                val marker = Marker(mapView).apply {
                    position = GeoPoint(shelter.lat, shelter.lng)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = shelter.name
                }

                mapView.overlays.add(marker)
            }

            mapView.invalidate()
        }
    )
}