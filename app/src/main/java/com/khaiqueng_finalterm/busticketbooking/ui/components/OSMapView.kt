package com.khaiqueng_finalterm.busticketbooking.ui.components

import android.preference.PreferenceManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OSMapView(
    modifier: Modifier = Modifier,
    latitude: Double = 16.047079, // Da Nang
    longitude: Double = 108.206230,
    zoomLevel: Double = 12.0,
    markerTitle: String = "Bến xe trung tâm"
) {
    val context = LocalContext.current

    // Khởi tạo cấu hình cho OSMDroid
    val mapConfiguration = remember {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = context.packageName
        true
    }

    // Tạo MapView gắn với vòng đời Compose
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            
            val mapController = controller
            mapController.setZoom(zoomLevel)
            
            val startPoint = GeoPoint(latitude, longitude)
            mapController.setCenter(startPoint)

            // Thêm Marker (Ghim)
            val startMarker = Marker(this)
            startMarker.position = startPoint
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            startMarker.title = markerTitle
            overlays.add(startMarker)
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        update = { view ->
            val point = GeoPoint(latitude, longitude)
            view.controller.setZoom(zoomLevel)
            view.controller.animateTo(point)
            view.overlays.clear()
            val marker = Marker(view).apply {
                position = point
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = markerTitle
            }
            view.overlays.add(marker)
            view.invalidate()
        },
        modifier = modifier
    )
}
