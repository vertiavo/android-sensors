package com.verti.sensors

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log

import kotlinx.android.synthetic.main.activity_pressure.*

class PressureActivity : Activity(), SensorEventListener {

    private val error = "ERROR"
    private val proximity = "PROXIMITY"
    private val accuracy = "ACCURACY"

    private lateinit var sensorManager: SensorManager
    private lateinit var proximitySensor: Sensor
    private val sensorSensitivity = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pressure)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor == null) {
            Log.d(error, "Sensor not found.")
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d(accuracy, "Accuracy changed!")

    }

    override fun onSensorChanged(p0: SensorEvent?) {

        if (p0 != null && p0.sensor.type == Sensor.TYPE_PROXIMITY) {
            if (p0.values[0] >= -sensorSensitivity && p0.values[0] <= sensorSensitivity) {
                shapeView.setBackgroundColor(Color.RED)
                Log.d(proximity, "near")
            } else {
                shapeView.setBackgroundColor(Color.GREEN)

                Log.d(proximity, "far")
            }
        }
    }
}
