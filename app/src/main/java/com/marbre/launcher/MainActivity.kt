package com.marbre.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StarField() }
    }
}

private data class Star(val x: Float, val y: Float, val size: Float, val phase: Float)

@Composable
fun StarField() {
    val stars = remember {
        List(220) {
            Star(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat() * 2.2f + 0.6f,
                Random.nextFloat() * 6.28f
            )
        }
    }
    val t by rememberInfiniteTransition(label = "twinkle").animateFloat(
        initialValue = 0f,
        targetValue = 6.2831855f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "t"
    )
    Canvas(Modifier.fillMaxSize().background(Color(0xFF05060F))) {
        stars.forEach { s ->
            val alpha = 0.35f + 0.65f * ((sin(t + s.phase) + 1f) / 2f)
            val base = if (s.size > 2f) Color(0xFFE8C872) else Color.White
            drawCircle(
                color = base.copy(alpha = alpha),
                radius = s.size,
                center = Offset(s.x * size.width, s.y * size.height)
            )
        }
    }
}