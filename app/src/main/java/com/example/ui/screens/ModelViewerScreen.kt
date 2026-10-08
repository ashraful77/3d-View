package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ModelRepository
import com.example.domain.Capability
import com.example.viewer3d.engine.Camera3D
import com.example.viewer3d.engine.CameraPreset
import com.example.viewer3d.engine.ShadingMode
import com.example.viewer3d.engine.ViewerState
import com.example.viewer3d.model.Mesh3D
import com.example.viewer3d.ui.BottomPresentationDock
import com.example.viewer3d.ui.EducationalInfoSheet
import com.example.viewer3d.ui.OrientationIndicator
import com.example.viewer3d.ui.PartInspectorSheet
import com.example.viewer3d.ui.QuickZoomControls
import com.example.viewer3d.ui.TopPresentationBar
import com.example.viewer3d.ui.Viewer3DCanvas
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelViewerScreen(
    modelId: String,
    repository: ModelRepository,
    onBack: () -> Unit
) {
    val model = remember(modelId) { repository.getModel(modelId) }
    val bookmarkedIds by repository.bookmarkedIds.collectAsState()
    val isBookmarked = remember(modelId, bookmarkedIds) { bookmarkedIds.contains(modelId) }

    if (model == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF090D16)),
            contentAlignment = Alignment.Center
        ) {
            Text("Model not found", color = Color.White)
        }
        return
    }

    // Base mesh for this model
    val baseMesh = remember(modelId) { repository.getMeshForModel(modelId) }

    // Viewer states
    var camera by remember { mutableStateOf(Camera3D.DEFAULT) }
    var autoRotate by remember { mutableStateOf(false) }
    var autoRotateSpeed by remember { mutableFloatStateOf(0.75f) }
    var shadingMode by remember { mutableStateOf(ShadingMode.SOLID_SHADED) }
    var selectedPartId by remember { mutableStateOf<String?>(null) }
    var showLabels by remember { mutableStateOf(true) }
    var showDimensions by remember { mutableStateOf(true) }
    var isPlayingAnimation by remember { mutableStateOf(model.id == "heart" && model.animationInfo?.canPlay == true) }
    var animationProgress by remember { mutableFloatStateOf(0f) }
    var netUnfoldProgress by remember { mutableFloatStateOf(0f) }
    var isNetUnfoldAnimating by remember { mutableStateOf(false) }
    var heartbeatBpm by remember { mutableIntStateOf(model.animationInfo?.defaultBpm ?: 72) }
    var isFullscreen by remember { mutableStateOf(false) }

    // Bottom sheet for educational info
    var showInfoSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Handle Android back button
    BackHandler {
        if (isFullscreen) {
            isFullscreen = false
        } else if (selectedPartId != null) {
            selectedPartId = null
        } else {
            onBack()
        }
    }

    // Auto-Rotate Loop
    LaunchedEffect(autoRotate, autoRotateSpeed) {
        if (autoRotate) {
            while (isActive) {
                camera = camera.orbit(deltaYaw = autoRotateSpeed, deltaPitch = 0f)
                delay(16) // ~60 fps
            }
        }
    }

    // Cardiac Pulse Animation Loop (only for models that have continuous animation like heart)
    LaunchedEffect(isPlayingAnimation, heartbeatBpm, model.id) {
        if (isPlayingAnimation && model.id == "heart") {
            val cycleMs = (60_000f / heartbeatBpm).toLong()
            val stepMs = 20L
            while (isActive) {
                val stepFraction = stepMs.toFloat() / cycleMs
                animationProgress = (animationProgress + stepFraction) % 1.0f
                delay(stepMs)
            }
        }
    }

    // Net Unfolding Animation Loop (smoothly oscillates between 0f and 1f)
    LaunchedEffect(isNetUnfoldAnimating) {
        if (isNetUnfoldAnimating) {
            var forward = true
            while (isActive) {
                val step = 0.02f
                if (forward) {
                    netUnfoldProgress += step
                    if (netUnfoldProgress >= 1f) {
                        netUnfoldProgress = 1f
                        delay(600) // pause at fully flat net
                        forward = false
                    }
                } else {
                    netUnfoldProgress -= step
                    if (netUnfoldProgress <= 0f) {
                        netUnfoldProgress = 0f
                        delay(600) // pause at fully folded solid
                        forward = true
                    }
                }
                delay(24)
            }
        }
    }

    // Compute morphed mesh based on Net Unfold or Cardiac Animation
    val activeMesh by remember(baseMesh, shadingMode, netUnfoldProgress, animationProgress, isPlayingAnimation) {
        derivedStateOf {
            when {
                shadingMode == ShadingMode.NET_UNFOLD && baseMesh.netMorpher != null -> {
                    baseMesh.netMorpher.invoke(netUnfoldProgress)
                }
                isPlayingAnimation && baseMesh.animationMorpher != null -> {
                    baseMesh.animationMorpher.invoke(animationProgress)
                }
                else -> baseMesh
            }
        }
    }

    val viewerState = remember(
        camera, autoRotate, autoRotateSpeed, shadingMode,
        selectedPartId, showLabels, showDimensions,
        isPlayingAnimation, animationProgress, netUnfoldProgress,
        heartbeatBpm, isFullscreen
    ) {
        ViewerState(
            camera = camera,
            autoRotate = autoRotate,
            autoRotateSpeed = autoRotateSpeed,
            shadingMode = shadingMode,
            selectedPartId = selectedPartId,
            showLabels = showLabels,
            showDimensions = showDimensions,
            isPlayingAnimation = isPlayingAnimation,
            animationProgress = animationProgress,
            netUnfoldProgress = netUnfoldProgress,
            heartbeatBpm = heartbeatBpm,
            isFullscreen = isFullscreen
        )
    }

    val selectedPart = remember(selectedPartId, model.parts) {
        model.parts.find { it.id == selectedPartId }
    }

    // Adaptive layout based on available width (phone vs tablet)
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .testTag("model_viewer_screen")
    ) {
        val isTabletLandscape = maxWidth >= 700.dp && !isFullscreen

        if (isTabletLandscape) {
            // Tablet Side-by-Side Presentation Layout
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Pane: Model Details & Parts Selector
                Surface(
                    modifier = Modifier
                        .width(340.dp)
                        .fillMaxHeight(),
                    color = Color(0xFF111827),
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = onBack) {
                                Text("←", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { repository.toggleBookmark(model.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = Color(0xFF38BDF8)
                                )
                            }
                        }

                        Text(
                            text = model.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${model.subjectId.rawId.replaceFirstChar { it.uppercase() }} • ${model.classLevel}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF38BDF8)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = model.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Parts Quick Selector
                        Text(
                            text = "Interactive Parts (${model.parts.size})",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        model.parts.forEach { part ->
                            val isPartSelected = (selectedPartId == part.id)
                            Surface(
                                onClick = {
                                    selectedPartId = if (isPartSelected) null else part.id
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isPartSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(Color(part.colorHex), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = part.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        fontWeight = if (isPartSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Full Lesson Guide button
                        Surface(
                            onClick = {
                                coroutineScope.launch { showInfoSheet = true }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Open Teacher Lesson Guide",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Right Pane: Full Interactive 3D Viewer
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Viewer3DCanvas(
                        mesh = activeMesh,
                        state = viewerState,
                        labels = model.labels,
                        onCameraChange = { deltaYaw, deltaPitch, scaleFactor, panX, panY ->
                            camera = camera
                                .orbit(deltaYaw, deltaPitch)
                                .zoom(scaleFactor)
                                .pan(panX, panY)
                        },
                        onPartTapped = { hitPartId ->
                            selectedPartId = if (selectedPartId == hitPartId) null else hitPartId
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Quick Zoom on Right
                    QuickZoomControls(
                        onZoomIn = { camera = camera.zoom(1.2f) },
                        onZoomOut = { camera = camera.zoom(0.83f) },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )

                    // Orientation Indicator on Top-Right
                    OrientationIndicator(
                        yaw = camera.yaw,
                        pitch = camera.pitch,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )

                    // Floating Dock at Bottom
                    BottomPresentationDock(
                        model = model,
                        state = viewerState,
                        onResetCamera = { camera = camera.reset() },
                        onToggleAutoRotate = { autoRotate = !autoRotate },
                        onPresetSelected = { preset -> camera = camera.applyPreset(preset) },
                        onShadingModeSelected = { mode -> shadingMode = mode },
                        onToggleLabels = { showLabels = !showLabels },
                        onToggleDimensions = { showDimensions = !showDimensions },
                        onNetUnfoldProgressChange = {
                            isNetUnfoldAnimating = false
                            netUnfoldProgress = it
                        },
                        onToggleNetUnfoldAnimation = {
                            isNetUnfoldAnimating = !isNetUnfoldAnimating
                        },
                        isNetUnfoldAnimating = isNetUnfoldAnimating,
                        onToggleAnimation = { isPlayingAnimation = !isPlayingAnimation },
                        onBpmChange = { heartbeatBpm = it },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )

                    // Part Inspector Card
                    PartInspectorSheet(
                        part = selectedPart,
                        allParts = model.parts,
                        onSelectPart = { selectedPartId = it },
                        onDeselect = { selectedPartId = null },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }
            }
        } else {
            // Handheld / Standard Mobile Layout
            Box(modifier = Modifier.fillMaxSize()) {
                // Interactive 3D Canvas
                Viewer3DCanvas(
                    mesh = activeMesh,
                    state = viewerState,
                    labels = model.labels,
                    onCameraChange = { deltaYaw, deltaPitch, scaleFactor, panX, panY ->
                        camera = camera
                            .orbit(deltaYaw, deltaPitch)
                            .zoom(scaleFactor)
                            .pan(panX, panY)
                    },
                    onPartTapped = { hitPartId ->
                        selectedPartId = if (selectedPartId == hitPartId) null else hitPartId
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Top Presentation Bar (hidden in fullscreen mode)
                if (!isFullscreen) {
                    TopPresentationBar(
                        model = model,
                        state = viewerState,
                        isBookmarked = isBookmarked,
                        onBack = onBack,
                        onToggleFullscreen = { isFullscreen = true },
                        onOpenInfoSheet = {
                            coroutineScope.launch { showInfoSheet = true }
                        },
                        onToggleBookmark = { repository.toggleBookmark(model.id) },
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    // Orientation Indicator
                    OrientationIndicator(
                        yaw = camera.yaw,
                        pitch = camera.pitch,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 58.dp)
                    )
                } else {
                    // Floating minimal fullscreen exit pill
                    Surface(
                        onClick = { isFullscreen = false },
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xCC1E293B),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .testTag("exit_fullscreen_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FullscreenExit,
                                contentDescription = "Exit Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Exit Fullscreen",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }

                // Quick Zoom on Center-Right
                QuickZoomControls(
                    onZoomIn = { camera = camera.zoom(1.2f) },
                    onZoomOut = { camera = camera.zoom(0.83f) },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )

                // Parts Chips Ribbon (for quick 1-tap part inspection during lecture)
                if (!isFullscreen && selectedPartId == null) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 64.dp)
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        model.parts.forEach { part ->
                            Surface(
                                onClick = { selectedPartId = part.id },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xD9111827),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(part.colorHex), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = part.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Part Inspector Card
                PartInspectorSheet(
                    part = selectedPart,
                    allParts = model.parts,
                    onSelectPart = { selectedPartId = it },
                    onDeselect = { selectedPartId = null },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = if (isFullscreen) 16.dp else 64.dp)
                )

                // Bottom Presentation Controls Dock
                BottomPresentationDock(
                    model = model,
                    state = viewerState,
                    onResetCamera = { camera = camera.reset() },
                    onToggleAutoRotate = { autoRotate = !autoRotate },
                    onPresetSelected = { preset -> camera = camera.applyPreset(preset) },
                    onShadingModeSelected = { mode ->
                        shadingMode = mode
                        if (mode != ShadingMode.NET_UNFOLD) {
                            isNetUnfoldAnimating = false
                        }
                    },
                    onToggleLabels = { showLabels = !showLabels },
                    onToggleDimensions = { showDimensions = !showDimensions },
                    onNetUnfoldProgressChange = {
                        isNetUnfoldAnimating = false
                        netUnfoldProgress = it
                    },
                    onToggleNetUnfoldAnimation = {
                        isNetUnfoldAnimating = !isNetUnfoldAnimating
                    },
                    isNetUnfoldAnimating = isNetUnfoldAnimating,
                    onToggleAnimation = { isPlayingAnimation = !isPlayingAnimation },
                    onBpmChange = { heartbeatBpm = it },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    // Modal Bottom Sheet for Educational Curriculum Guide
    if (showInfoSheet) {
        EducationalInfoSheet(
            model = model,
            sheetState = sheetState,
            onDismiss = { showInfoSheet = false }
        )
    }
}
