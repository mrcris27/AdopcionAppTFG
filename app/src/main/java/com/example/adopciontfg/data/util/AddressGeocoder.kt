package com.example.adopciontfg.data.util

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import java.util.Locale
import kotlin.coroutines.resume

suspend fun geocodeAddress(context: Context, address: String): GeoPoint? {
    val trimmed = address.trim()
    if (trimmed.isEmpty()) return null

    return withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocationName(trimmed, 1) { locations ->
                        val location = locations.firstOrNull()
                        val point = location?.let { GeoPoint(it.latitude, it.longitude) }
                        if (continuation.isActive) continuation.resume(point)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val location = geocoder.getFromLocationName(trimmed, 1)?.firstOrNull()
                location?.let { GeoPoint(it.latitude, it.longitude) }
            }
        } catch (_: Exception) {
            null
        }
    }
}
