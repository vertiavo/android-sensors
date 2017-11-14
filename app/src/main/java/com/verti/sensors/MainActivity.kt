package com.verti.sensors

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), SensorEventListener {

    private val error = "ERROR"
    private val pressure = "PRESSURE"
    private val accuracy = "ACCURACY"

    private lateinit var sensorManager: SensorManager
    private lateinit var pressureSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        val deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

        sensorQuantityView.text =
                resources.getQuantityString(R.plurals.sensors_summary_info, deviceSensors.size, deviceSensors.size)

        for (sensor in deviceSensors) {
            sensorView.append(sensor.name)
            sensorView.append("\n")
        }

        if (pressureSensor == null) {
            Log.d(error, "Sensor not found.")
        }

        nextButton.setOnClickListener { startActivity(Intent(this, PressureActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d(accuracy, "Accuracy changed!")
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0 != null && p0.sensor.type == Sensor.TYPE_PRESSURE) {
            Log.d(pressure, p0.values[0].toString())
        }
    }
}
