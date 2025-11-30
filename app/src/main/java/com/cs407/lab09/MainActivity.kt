package com.cs407.lab09

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.lab09.ui.theme.Lab09Theme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private val viewModel: BallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: BallViewModel) {

    val context = LocalContext.current

    // ✔ Initialize SensorManager
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    // ✔ Get gravity sensor
    val gravitySensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    // ✔ Sensor listener setup via DisposableEffect
    DisposableEffect(sensorManager, gravitySensor) {

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    viewModel.onSensorDataChanged(it)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (gravitySensor != null) {
            sensorManager.registerListener(
                listener,
                gravitySensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            if (gravitySensor != null) {
                sensorManager.unregisterListener(listener, gravitySensor)
            }
        }
    }

    // ---------- UI ----------
    Column(modifier = Modifier.fillMaxSize()) {

        // ✔ Reset button
        Button(
            onClick = { viewModel.reset() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text(text = "Reset")
        }

        // Ball size definition
        val ballSize = 50.dp
        val ballSizePx = with(LocalDensity.current) { ballSize.toPx() }

        // ✔ Collect ball position from ViewModel
        val ballPosition by viewModel.ballPosition.collectAsStateWithLifecycle()

        // Game field box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .paint(
                    painter = painterResource(id = R.drawable.field),
                    contentScale = ContentScale.FillBounds
                )
                .onSizeChanged { size ->
                    // ✔ Notify ViewModel of field size
                    viewModel.initBall(
                        fieldWidth = size.width.toFloat(),
                        fieldHeight = size.height.toFloat(),
                        ballSizePx = ballSizePx
                    )
                }
        ) {

            // ✔ Draw ball
            Image(
                painter = painterResource(id = R.drawable.soccer),
                contentDescription = "Soccer Ball",
                modifier = Modifier
                    .size(ballSize)
                    .offset {
                        IntOffset(
                            x = ballPosition.x.roundToInt(),
                            y = ballPosition.y.roundToInt()
                        )
                    }
            )
        }
    }
}