package com.verti.sensors

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_proximity.*


class ProximityActivity : Activity(), SensorEventListener {

    private val error = "ERROR"
    private val proximity = "PROXIMITY"
    private val accuracy = "ACCURACY"

    private lateinit var sensorManager: SensorManager
    private lateinit var proximitySensor: Sensor
    private lateinit var accelerometer: Sensor
    private val sensorSensitivity = 4
    private var closeUpCounter = 0
    private var alreadyCloseUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proximity)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (proximitySensor == null && accelerometer == null) {
            Log.d(error, "Sensor not found.")
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL
        )
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
                Log.d(proximity, "near")
                shapeView.setBackgroundColor(Color.RED)
                shapeTitle.textSize = resources.getDimension(R.dimen.smallFont)
                if (!alreadyCloseUp) {
                    alreadyCloseUp = true
                    closeUpCounter++
                    checkCloseUpCounter()
                }
            } else {
                Log.d(proximity, "far")
                shapeView.setBackgroundColor(Color.GREEN)
                shapeTitle.textSize = resources.getDimension(R.dimen.largeFont)
                if (alreadyCloseUp) {
                    alreadyCloseUp = false
                }
            }
        }

        if (p0 != null && p0.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val display = windowManager.defaultDisplay
            val windowSize = Point()
            display.getSize(windowSize)

            val shapeHeight = resources.getDimension(R.dimen.shapeHeight)
            val shapeWidth = resources.getDimension(R.dimen.shapeWidth)

            // Device rotated to the left
            if (p0.values[0] > 0 && shapeView.x > 0) {
                shapeView.translationX -= p0.values[0]
            }

            // Device rotated to the right
            if (p0.values[0] < 0 && (shapeView.x + shapeWidth) < windowSize.x) {
                shapeView.translationX -= p0.values[0]
            }

            // Device rotated forward
            if (p0.values[1] < 0 && shapeView.y > 0) {
                shapeView.translationY += p0.values[1]
            }

            // Device rotated backward
            if (p0.values[1] > 0 && (shapeView.y + shapeHeight) < windowSize.y) {
                shapeView.translationY += p0.values[1]
            }
        }
    }

    private fun checkCloseUpCounter() {
        if (closeUpCounter == 2) {
            Toast.makeText(applicationContext, getString(R.string.warning_closing_app), Toast.LENGTH_SHORT).show()
        } else if (closeUpCounter > 2) {
            this.finish()
            System.exit(0)
        }
    }
}
