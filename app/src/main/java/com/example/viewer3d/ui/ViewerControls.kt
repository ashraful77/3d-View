package com.example.viewer3d.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.Capability
import com.example.domain.ModelObject3D
import com.example.viewer3d.engine.CameraPreset
import com.example.viewer3d.engine.ShadingMode
import com.example.viewer3d.engine.ViewerState

@Composable
fun TopPresentationBar(
    model: ModelObject3D,
    state: ViewerState,
    isBookmarked: Boolean,
    onBack: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onOpenInfoSheet: () -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_presentation_bar"),
        color = Color(0xD9090D16),
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.testTag("back_button")
                ) {
                    Text(
                        text = "←",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${model.subjectId.rawId.replaceFirstChar { it.uppercase() }} • ${model.classLevel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF38BDF8)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Bookmark toggle
                IconButton(
                    onClick = onToggleBookmark,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.size(38.dp).testTag("bookmark_button")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) Color(0xFFF59E0B) else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Educational Info
                IconButton(
                    onClick = onOpenInfoSheet,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.size(38.dp).testTag("info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Educational Info",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Fullscreen toggle
                IconButton(
                    onClick = onToggleFullscreen,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier.size(38.dp).testTag("fullscreen_button")
                ) {
                    Icon(
                        imageVector = if (state.isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Toggle Fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(12.dp)
            .testTag("quick_zoom_controls"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            onClick = onZoomIn,
            shape = CircleShape,
            color = Color(0xCC1E293B),
            shadowElevation = 4.dp,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Zoom In",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Surface(
            onClick = onZoomOut,
            shape = CircleShape,
            color = Color(0xCC1E293B),
            shadowElevation = 4.dp,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Zoom Out",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun OrientationIndicator(
    yaw: Float,
    pitch: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xCC111827),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = modifier.padding(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Yaw ${(yaw % 360).toInt()}° • Pitch ${pitch.toInt()}°",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun BottomPresentationDock(
    model: ModelObject3D,
    state: ViewerState,
    onResetCamera: () -> Unit,
    onToggleAutoRotate: () -> Unit,
    onPresetSelected: (CameraPreset) -> Unit,
    onShadingModeSelected: (ShadingMode) -> Unit,
    onToggleLabels: () -> Unit,
    onToggleDimensions: () -> Unit,
    onNetUnfoldProgressChange: (Float) -> Unit,
    onToggleNetUnfoldAnimation: () -> Unit,
    isNetUnfoldAnimating: Boolean,
    onToggleAnimation: () -> Unit,
    onBpmChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bottom_presentation_dock"),
        color = Color(0xF2090D16),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        tonalElevation = 10.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Contextual dynamic sliders
            // 1. Net Unfolding slider for geometry models
            AnimatedVisibility(
                visible = model.capabilities.contains(Capability.NET_UNFOLD) &&
                        state.shadingMode == ShadingMode.NET_UNFOLD
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "2D Net Unfold: ${(state.netUnfoldProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Auto animate button
                            Surface(
                                onClick = onToggleNetUnfoldAnimation,
                                shape = RoundedCornerShape(8.dp),
                                color = if (isNetUnfoldAnimating) Color(0xFF0284C7) else Color(0xFF1E293B)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isNetUnfoldAnimating) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isNetUnfoldAnimating) "Pause" else "Animate",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (state.netUnfoldProgress < 0.1f) "Folded 3D Solid" else "Flat 2D Planar Net",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Slider(
                        value = state.netUnfoldProgress,
                        onValueChange = onNetUnfoldProgressChange,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E5FF),
                            activeTrackColor = Color(0xFF0284C7),
                            inactiveTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("net_unfold_slider")
                    )
                }
            }

            // 2. Cardiac BPM controller for heart
            AnimatedVisibility(
                visible = model.id == "heart" && model.capabilities.contains(Capability.ANIMATION)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onToggleAnimation,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (state.isPlayingAnimation) Color(0xFFEF4444) else Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("heartbeat_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (state.isPlayingAnimation) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Pulse",
                            tint = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Heart Rate: ${state.heartbeatBpm} BPM",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFF87171),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (state.heartbeatBpm > 100) "Tachycardia / Exercise" else "Normal Resting Rate",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Slider(
                            value = state.heartbeatBpm.toFloat(),
                            onValueChange = { onBpmChange(it.toInt()) },
                            valueRange = 50f..140f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFEF4444),
                                activeTrackColor = Color(0xFFDC2626),
                                inactiveTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }

            // Shading Modes Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Solid Mode
                FilterChip(
                    selected = state.shadingMode == ShadingMode.SOLID_SHADED,
                    onClick = { onShadingModeSelected(ShadingMode.SOLID_SHADED) },
                    label = { Text("Solid") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White
                    )
                )

                // Wireframe Mode
                FilterChip(
                    selected = state.shadingMode == ShadingMode.TECHNICAL_WIREFRAME,
                    onClick = { onShadingModeSelected(ShadingMode.TECHNICAL_WIREFRAME) },
                    label = { Text("Wireframe") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White
                    )
                )

                // Cutaway / X-Ray Mode (if supported)
                if (model.capabilities.contains(Capability.CUTAWAY_XRAY)) {
                    FilterChip(
                        selected = state.shadingMode == ShadingMode.CUTAWAY_XRAY,
                        onClick = { onShadingModeSelected(ShadingMode.CUTAWAY_XRAY) },
                        label = { Text("Cutaway / X-Ray") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // 2D Net Unfold Mode (if supported)
                if (model.capabilities.contains(Capability.NET_UNFOLD)) {
                    FilterChip(
                        selected = state.shadingMode == ShadingMode.NET_UNFOLD,
                        onClick = { onShadingModeSelected(ShadingMode.NET_UNFOLD) },
                        label = { Text("2D Net Unfold") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // Labels Toggle
                FilterChip(
                    selected = state.showLabels,
                    onClick = onToggleLabels,
                    label = { Text("Labels") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Tag,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0F766E),
                        selectedLabelColor = Color.White
                    )
                )

                // Dimensions Toggle (if supported)
                if (model.capabilities.contains(Capability.DIMENSIONS)) {
                    FilterChip(
                        selected = state.showDimensions,
                        onClick = onToggleDimensions,
                        label = { Text("Dimensions") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFB45309),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Camera & Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Preset Angles
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CameraPreset.entries.forEach { preset ->
                        Surface(
                            onClick = { onPresetSelected(preset) },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.testTag("preset_${preset.name.lowercase()}")
                        ) {
                            Text(
                                text = preset.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Quick Tools (Reset, Auto-Rotate)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Auto-Rotate toggle
                    IconButton(
                        onClick = onToggleAutoRotate,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (state.autoRotate) Color(0xFF0284C7) else Color(0xFF1E293B)
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("auto_rotate_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RotateRight,
                            contentDescription = "Auto Rotate",
                            tint = if (state.autoRotate) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Reset Camera
                    IconButton(
                        onClick = onResetCamera,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("reset_camera_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Camera",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
