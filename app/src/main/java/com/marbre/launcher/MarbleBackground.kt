package com.marbre.launcher

import android.graphics.RuntimeShader
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ShaderBrush

private const val MARBLE_SHADER = """
uniform float2 resolution;
uniform float time;

float hash(float2 p) {
    p = fract(p * float2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float noise(float2 p) {
    float2 i = floor(p);
    float2 f = fract(p);
    float a = hash(i);
    float b = hash(i + float2(1.0, 0.0));
    float c = hash(i + float2(0.0, 1.0));
    float d = hash(i + float2(1.0, 1.0));
    float2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float fbm(float2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 5; i++) {
        value += amplitude * noise(p);
        p *= 2.0;
        amplitude *= 0.5;
    }
    return value;
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution.xy;
    float2 p = uv * 3.0;

    float slowTime = time * 0.04;
    p.x += slowTime * 0.4;

    float n = fbm(p + fbm(p + slowTime));

    float veins = abs(sin((uv.x * 6.0 + n * 4.0 + slowTime) * 3.14159));
    veins = pow(1.0 - veins, 8.0);

    float3 black = float3(0.02, 0.02, 0.035);
    float3 darkGold = float3(0.22, 0.16, 0.05);
    float3 gold = float3(0.85, 0.65, 0.25);
    float3 brightGold = float3(1.0, 0.87, 0.55);

    float3 color = mix(black, darkGold, n * 0.6);
    color = mix(color, gold, veins);
    color = mix(color, brightGold, veins * veins * 0.5);

    return half4(color, 1.0);
}
"""

@Composable
fun MarbleBackground(modifier: Modifier = Modifier) {
    val shader = remember { RuntimeShader(MARBLE_SHADER) }
    val time = remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> time.value = (now - start) / 1_000_000_000f }
        }
    }

    Canvas(modifier) {
        shader.setFloatUniform("resolution", size.width, size.height)
        shader.setFloatUniform("time", time.value)
        drawRect(brush = ShaderBrush(shader))
    }
}