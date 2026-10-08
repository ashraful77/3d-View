package com.example.viewer3d.engine

import kotlin.math.PI

enum class CameraPreset(val label: String, val yaw: Float, val pitch: Float) {
    ISOMETRIC("3D Iso", 45f, 25f),
    FRONT("Front", 0f, 0f),
    TOP("Top", 0f, 85f),
    SIDE("Side", 90f, 0f)
}

data class Camera3D(
    val yaw: Float = 45f,
    val pitch: Float = 25f,
    val distance: Float = 5.2f,
    val panX: Float = 0f,
    val panY: Float = 0f,
    val fovDegrees: Float = 45f
) {
    val fovRadians: Float get() = fovDegrees * (PI.toFloat() / 180f)
    val yawRadians: Float get() = yaw * (PI.toFloat() / 180f)
    val pitchRadians: Float get() = pitch * (PI.toFloat() / 180f)

    fun orbit(deltaYaw: Float, deltaPitch: Float): Camera3D {
        val newYaw = (yaw + deltaYaw) % 360f
        val newPitch = (pitch + deltaPitch).coerceIn(-85f, 85f)
        return copy(yaw = newYaw, pitch = newPitch)
    }

    fun zoom(scaleFactor: Float): Camera3D {
        val newDistance = (distance / scaleFactor).coerceIn(2.0f, 16.0f)
        return copy(distance = newDistance)
    }

    fun pan(deltaX: Float, deltaY: Float): Camera3D {
        return copy(panX = (panX + deltaX).coerceIn(-4.0f, 4.0f), panY = (panY + deltaY).coerceIn(-4.0f, 4.0f))
    }

    fun applyPreset(preset: CameraPreset): Camera3D {
        return copy(yaw = preset.yaw, pitch = preset.pitch, panX = 0f, panY = 0f)
    }

    fun reset(): Camera3D = Camera3D()

    companion object {
        val DEFAULT = Camera3D()
    }
}
