package com.cs407.lab09

import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BallViewModel : ViewModel() {

    private var ball: Ball? = null
    private var lastTimestamp: Long = 0L

    private val ACC_SCALE = 25f


    private val _ballPosition = MutableStateFlow(Offset.Zero)
    val ballPosition: StateFlow<Offset> = _ballPosition.asStateFlow()

    /**
     * Called when the field size is known.
     */
    fun initBall(fieldWidth: Float, fieldHeight: Float, ballSizePx: Float) {
        if (ball == null) {
            ball = Ball(
                backgroundWidth = fieldWidth,
                backgroundHeight = fieldHeight,
                ballSize = ballSizePx
            )
            // Ball starts at the center
            _ballPosition.value = Offset(ball!!.posX, ball!!.posY)
        }
    }

    /**
     * Called on gravity sensor updates.
     */
    fun onSensorDataChanged(event: SensorEvent) {
        val currentBall = ball ?: return

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {

            if (lastTimestamp != 0L) {

                // Convert nanoseconds → seconds
                val NS2S = 1.0f / 1_000_000_000.0f
                val dT = (event.timestamp - lastTimestamp) * NS2S

                // Gravity sensor axes: +Y points OUT of the screen
                // Screen coordinates: +Y points DOWN
                // So invert Y
                val rawX = event.values[0]
                val rawY = event.values[1]

                val xAcc = -rawX * ACC_SCALE
                val yAcc = rawY * ACC_SCALE



                currentBall.updatePositionAndVelocity(
                    xAcc = xAcc,
                    yAcc = yAcc,
                    dT = dT
                )

                // Boundary checks
                currentBall.checkBoundaries()

                // Update UI
                _ballPosition.update {
                    Offset(currentBall.posX, currentBall.posY)
                }
            }

            // Update timestamp
            lastTimestamp = event.timestamp
        }
    }

    /**
     * Resets ball to center.
     */
    fun reset() {
        ball?.reset()

        ball?.let { b ->
            _ballPosition.value = Offset(b.posX, b.posY)
        }

        lastTimestamp = 0L
    }
}