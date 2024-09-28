package com.nnnikitaaa.trap.activities

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button

import com.nnnikitaaa.trap.R
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class MapPopupDialog(context: Context) : Dialog(context) {
    private lateinit var mapView: MapView
    private lateinit var map:  com.yandex.mapkit.map.Map

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.map_popup)

        mapView = findViewById(R.id.yandexMapView)
        map = mapView.mapWindow.map
        val closeButton: Button = findViewById(R.id.closeButton)

        closeButton.setOnClickListener {
            dismiss()
        }

        val ikit = Point(55.994446, 92.797586)
        val home = Point(56.060790, 92.905132)
        val krasnoyarsk = Point(56.010548, 92.852571)
        map.move( CameraPosition( krasnoyarsk,10f,0f,0f) )

        addBlackPin(ikit, "Институт космических и информационных технологий.")
        addBlackPin(home, "Дом")
        addBlackPin(krasnoyarsk, "Город Красноярск")
    }

    private fun addBlackPin(point: Point, description: String): PlacemarkMapObject {
        val imageProvider = ImageProvider.fromResource(context, R.drawable.black_pin)
        return map.mapObjects.addPlacemark().apply {
            geometry = point
            setIcon(imageProvider)
            setText(
                description,
                TextStyle().apply {
                    size = 8f
                    placement = TextStyle.Placement.RIGHT
                },
            )
        }
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }
}