package com.mr3y.compose_analogclock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import com.mr3y.compose_analogclock.ui.theme.ComposeAnalogClockTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogClock(modifier: Modifier = Modifier) {
    val fontFamilyResolver = LocalFontFamilyResolver.current
    var seconds by remember { mutableStateOf(0) }
    val minutes by remember { derivedStateOf { seconds / 60 } }

    // TODO: get the current local time
    val hour by remember { derivedStateOf { minutes / 60 } }
    LaunchedEffect(Unit) {
        tickerFlow().collect { seconds++ }
    }
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
    ) {
        val radius = 275f
        val quarters = setOf(0, 90, 180, 270)
        drawCircle(
            color = Color.LightGray,
            radius = radius,
            style =  Stroke(10f)
        )
        for (tickAngleInDegrees in 0 until 360 step 30) {
            val theta = tickAngleInDegrees.asRadian()
            val startRadius = radius * 0.92f
            val endRadius = radius * 0.8f
            val startPos = Offset(
                cos(theta) * startRadius,
                sin(theta) * startRadius
            )
            val endPos = Offset(
                cos(theta) * endRadius,
                sin(theta) * endRadius
            )
            drawLine(
                color = Color.LightGray,
                start = center + startPos,
                end = center + endPos,
                strokeWidth = 2f
            )
            val paragraph = Paragraph(
                text = if (tickAngleInDegrees == 0) "12" else (tickAngleInDegrees / 30).toString(),
                style = TextStyle(color = Color.DarkGray, fontSize = 10.sp),
                constraints = Constraints(
                    maxWidth = 35,
                    maxHeight = 35
                ),
                density = this,
                fontFamilyResolver = fontFamilyResolver
            )
            val numberBasePos = Offset(
                cos(theta - 90.asRadian()) * (endRadius * 0.9f),
                sin(theta - 90.asRadian()) * (endRadius * 0.9f),
            )

            translate(center.x + numberBasePos.x - (paragraph.maxIntrinsicWidth / 2f), center.y + numberBasePos.y - (paragraph.height / 2f)) {
                drawIntoCanvas {
                    paragraph.paint(it, color = if (tickAngleInDegrees in quarters) Color.DarkGray else Color.LightGray)
                }
            }
        }
        val hourHand = Path().apply {
            moveTo(center.x, center.y)
            lineTo(center.x + (center.x * 0.023f), center.y + (center.y * 0.023f))
            lineTo(center.x, center.y + (center.y * 0.25f))
            lineTo(center.x - (center.x * 0.023f), center.y + (center.y * 0.023f))
            close()
        }
        val minutesHand = Path().apply {
            moveTo(center.x, center.y)
            lineTo(center.x + (center.x * 0.023f), center.y + (center.y * 0.023f))
            lineTo(center.x, center.y + (center.y * 0.35f))
            lineTo(center.x - (center.x * 0.023f), center.y + (center.y * 0.023f))
            close()
        }
        rotate((6 * seconds).toFloat() + 180f) {
            drawLine(
                color = Color.Red,
                start = center,
                end = center + Offset(0f, radius * 0.8f),
                strokeWidth = 2f
            )
        }
        rotate((6 * minutes).toFloat() + 180f) {
            drawPath(minutesHand, Color.DarkGray)
        }
        rotate((30 * hour + minutes / 2).toFloat() + 180f) {
            drawPath(hourHand, Color.Black)
        }
    }
}

private fun tickerFlow() = flow {
    do {
        emit(Unit)
        delay(1000L)
    } while (true)
}

private fun Int.asRadian() = this * PI.toFloat() / 180f

@Preview(showBackground = true)
@Composable
fun AnalogClockPreview() {
    ComposeAnalogClockTheme {
        AnalogClock()
    }
}