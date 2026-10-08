package com.example.viewer3d.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.domain.ModelLabel
import com.example.viewer3d.engine.RenderDimension
import com.example.viewer3d.engine.RenderLabel
import com.example.viewer3d.engine.RenderLightRay
import com.example.viewer3d.engine.RenderScene
import com.example.viewer3d.engine.Renderer3D
import com.example.viewer3d.engine.ViewerState
import com.example.viewer3d.model.Mesh3D

@Composable
fun Viewer3DCanvas(
    mesh: Mesh3D,
    state: ViewerState,
    labels: List<ModelLabel>,
    onCameraChange: (deltaYaw: Float, deltaPitch: Float, scaleFactor: Float, panX: Float, panY: Float) -> Unit,
    onPartTapped: (partId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    var canvasSize by remember { mutableStateOf(Offset(1080f, 1920f)) }

    val currentOnCameraChange by rememberUpdatedState(onCameraChange)
    val currentOnPartTapped by rememberUpdatedState(onPartTapped)

    // Compute rendered scene
    val scene by remember(mesh, state, labels, canvasSize) {
        derivedStateOf {
            Renderer3D.render(
                mesh = mesh,
                camera = state.camera,
                viewportWidth = canvasSize.x.coerceAtLeast(100f),
                viewportHeight = canvasSize.y.coerceAtLeast(100f),
                state = state,
                labelsData = labels
            )
        }
    }

    val currentScene by rememberUpdatedState(scene)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .testTag("viewer_3d_canvas")
            .pointerInput(Unit) {
                // Unified touch detector: single-finger drag = orbit, multi-touch = zoom/pan, tap = part select
                detectTransformGestures(
                    panZoomLock = false,
                    onGesture = { centroid, pan, zoom, rotation ->
                        val zoomFactor = if (zoom.isFinite() && zoom > 0.05f) zoom else 1.0f
                        val panDeltaX = if (pan.x.isFinite()) pan.x / 400f else 0f
                        val panDeltaY = if (pan.y.isFinite()) pan.y / 400f else 0f

                        val deltaYaw = if (pan.x.isFinite()) pan.x * 0.45f else 0f
                        val deltaPitch = if (pan.y.isFinite()) -pan.y * 0.45f else 0f

                        currentOnCameraChange(deltaYaw, deltaPitch, zoomFactor, panDeltaX, panDeltaY)
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        val hitPartId = Renderer3D.hitTestPart(tapOffset, currentScene)
                        currentOnPartTapped(hitPartId)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (size.width > 10f && size.height > 10f) {
                canvasSize = Offset(size.width, size.height)
            }

            // 1. Subtle scientific laboratory background grid
            drawLabGrid(size.width, size.height)

            // 2. Draw 3D Faces (sorted back-to-front by depth, with strict finite coordinate guards)
            val facePath = Path()
            for (face in scene.faces) {
                if (!face.p0.x.isFinite() || !face.p0.y.isFinite() ||
                    !face.p1.x.isFinite() || !face.p1.y.isFinite() ||
                    !face.p2.x.isFinite() || !face.p2.y.isFinite()
                ) continue

                facePath.reset()
                facePath.moveTo(face.p0.x, face.p0.y)
                facePath.lineTo(face.p1.x, face.p1.y)
                facePath.lineTo(face.p2.x, face.p2.y)
                facePath.close()

                drawPath(path = facePath, color = face.color, style = Fill)

                val strokeWidth = if (face.isSelected) 3.0f else 1.0f
                drawPath(path = facePath, color = face.edgeColor, style = Stroke(width = strokeWidth))
            }

            // 3. Draw explicit edges (wireframe mode and boundary rings)
            for (edge in scene.edges) {
                if (edge.p0.x.isFinite() && edge.p0.y.isFinite() &&
                    edge.p1.x.isFinite() && edge.p1.y.isFinite()
                ) {
                    drawLine(
                        color = edge.color,
                        start = edge.p0,
                        end = edge.p1,
                        strokeWidth = edge.strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 4. Draw Light Rays / Blood flow indicators
            for (ray in scene.lightRays) {
                if (ray.start.x.isFinite() && ray.start.y.isFinite() &&
                    ray.end.x.isFinite() && ray.end.y.isFinite()
                ) {
                    drawLightRay(ray, textMeasurer)
                }
            }

            // 5. Draw Dimension measurement lines & brackets
            if (state.showDimensions) {
                for (dim in scene.dimensions) {
                    if (dim.start.x.isFinite() && dim.start.y.isFinite() &&
                        dim.end.x.isFinite() && dim.end.y.isFinite() &&
                        dim.textPos.x.isFinite() && dim.textPos.y.isFinite()
                    ) {
                        drawDimensionLine(dim, textMeasurer)
                    }
                }
            }

            // 6. Draw 3D floating educational labels
            if (state.showLabels) {
                for (label in scene.labels) {
                    if (label.screenPos.x.isFinite() && label.screenPos.y.isFinite()) {
                        drawFloatingLabel(label, textMeasurer)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawLabGrid(width: Float, height: Float) {
    val gridColor = Color(0x1038BDF8)
    val step = 64f
    var x = 0f
    while (x <= width) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
        x += step
    }
    var y = 0f
    while (y <= height) {
        drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
        y += step
    }
}

private fun DrawScope.drawLightRay(ray: RenderLightRay, textMeasurer: TextMeasurer) {
    drawLine(
        color = ray.color,
        start = ray.start,
        end = ray.end,
        strokeWidth = ray.strokeWidth,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = ray.color,
        radius = ray.strokeWidth * 1.5f,
        center = ray.end
    )
    if (ray.label.isNotEmpty()) {
        try {
            val textLayout = textMeasurer.measure(
                text = ray.label,
                style = TextStyle(fontSize = 11.sp, color = ray.color, fontWeight = FontWeight.Bold)
            )
            val textX = ray.end.x + 8f
            val textY = ray.end.y - 8f
            if (textX >= 0f && textX + textLayout.size.width <= size.width &&
                textY >= 0f && textY + textLayout.size.height <= size.height
            ) {
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(textX, textY)
                )
            }
        } catch (_: Exception) {
            // Safe fallback
        }
    }
}

private fun DrawScope.drawDimensionLine(dim: RenderDimension, textMeasurer: TextMeasurer) {
    val lineColor = Color(0xFFF59E0B)
    drawLine(
        color = lineColor,
        start = dim.start,
        end = dim.end,
        strokeWidth = 2.0f,
        cap = StrokeCap.Round
    )
    drawCircle(color = lineColor, radius = 3.5f, center = dim.start)
    drawCircle(color = lineColor, radius = 3.5f, center = dim.end)

    try {
        val textLayout = textMeasurer.measure(
            text = dim.label,
            style = TextStyle(fontSize = 11.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold)
        )
        val padX = 8f
        val padY = 4f
        val rawX = dim.textPos.x - textLayout.size.width / 2f - padX
        val rawY = dim.textPos.y - textLayout.size.height / 2f - padY

        val pillW = textLayout.size.width + padX * 2f
        val pillH = textLayout.size.height + padY * 2f

        // Only draw if pill is on-screen
        if (rawX >= 0f && rawX + pillW <= size.width &&
            rawY >= 0f && rawY + pillH <= size.height
        ) {
            drawRoundRect(
                color = Color(0xFFFDE68A),
                topLeft = Offset(rawX, rawY),
                size = androidx.compose.ui.geometry.Size(pillW, pillH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )
            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(rawX + padX, rawY + padY)
            )
        }
    } catch (_: Exception) {
        // Safe fallback prevents maxWidth constraint crash
    }
}

private fun DrawScope.drawFloatingLabel(lbl: RenderLabel, textMeasurer: TextMeasurer) {
    val bgColor = if (lbl.isSelected) Color(0xFF00E5FF) else Color(0xDD1E293B)
    val textColor = if (lbl.isSelected) Color(0xFF090D16) else Color(0xFFF1F5F9)
    val borderColor = if (lbl.isSelected) Color.White else Color(0xFF38BDF8)

    drawCircle(color = borderColor, radius = 4f, center = lbl.screenPos)

    try {
        val labelText = lbl.text
        val textLayout = textMeasurer.measure(
            text = labelText,
            style = TextStyle(fontSize = 11.sp, color = textColor, fontWeight = FontWeight.Bold)
        )
        val padX = 10f
        val padY = 5f
        val rawX = lbl.screenPos.x + 12f
        val rawY = lbl.screenPos.y - 12f
        val pillW = textLayout.size.width + padX * 2f
        val pillH = textLayout.size.height + padY * 2f

        if (rawX >= 0f && rawX + pillW <= size.width &&
            rawY >= 0f && rawY + pillH <= size.height
        ) {
            drawLine(
                color = borderColor,
                start = lbl.screenPos,
                end = Offset(rawX, rawY + pillH / 2f),
                strokeWidth = 1.2f
            )

            drawRoundRect(
                color = bgColor,
                topLeft = Offset(rawX, rawY),
                size = androidx.compose.ui.geometry.Size(pillW, pillH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
            )
            drawRoundRect(
                color = borderColor,
                topLeft = Offset(rawX, rawY),
                size = androidx.compose.ui.geometry.Size(pillW, pillH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f),
                style = Stroke(width = 1.2f)
            )

            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(rawX + padX, rawY + padY)
            )
        }
    } catch (_: Exception) {
        // Safe fallback
    }
}
