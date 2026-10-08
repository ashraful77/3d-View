package com.example.viewer3d.engine

enum class ShadingMode(val title: String, val iconName: String) {
    SOLID_SHADED("Solid", "view_in_ar"),
    TECHNICAL_WIREFRAME("Wireframe", "grid_4x4"),
    CUTAWAY_XRAY("Cutaway / X-Ray", "layers"),
    NET_UNFOLD("2D Net Unfold", "flip_to_front")
}

data class ViewerState(
    val camera: Camera3D = Camera3D.DEFAULT,
    val autoRotate: Boolean = false,
    val autoRotateSpeed: Float = 0.6f,
    val shadingMode: ShadingMode = ShadingMode.SOLID_SHADED,
    val selectedPartId: String? = null,
    val showLabels: Boolean = true,
    val showDimensions: Boolean = true,
    val isPlayingAnimation: Boolean = false,
    val animationProgress: Float = 0f,
    val netUnfoldProgress: Float = 0f,
    val heartbeatBpm: Int = 72,
    val isFullscreen: Boolean = false
)
