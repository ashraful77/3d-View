package com.example.viewer3d.math

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class Matrix4(val data: FloatArray = FloatArray(16)) {

    init {
        if (data.size != 16) throw IllegalArgumentException("Matrix4 requires 16 float elements")
    }

    operator fun get(row: Int, col: Int): Float = data[row * 4 + col]
    operator fun set(row: Int, col: Int, value: Float) {
        data[row * 4 + col] = value
    }

    operator fun times(other: Matrix4): Matrix4 {
        val result = FloatArray(16)
        for (i in 0..3) {
            for (j in 0..3) {
                var sum = 0f
                for (k in 0..3) {
                    sum += this[i, k] * other[k, j]
                }
                result[i * 4 + j] = sum
            }
        }
        return Matrix4(result)
    }

    fun transformPoint(p: Vector3): Vector3 {
        val x = data[0] * p.x + data[1] * p.y + data[2] * p.z + data[3]
        val y = data[4] * p.x + data[5] * p.y + data[6] * p.z + data[7]
        val z = data[8] * p.x + data[9] * p.y + data[10] * p.z + data[11]
        val w = data[12] * p.x + data[13] * p.y + data[14] * p.z + data[15]
        return if (w != 0f && w != 1f) {
            Vector3(x / w, y / w, z / w)
        } else {
            Vector3(x, y, z)
        }
    }

    fun transformDirection(dir: Vector3): Vector3 {
        val x = data[0] * dir.x + data[1] * dir.y + data[2] * dir.z
        val y = data[4] * dir.x + data[5] * dir.y + data[6] * dir.z
        val z = data[8] * dir.x + data[9] * dir.y + data[10] * dir.z
        return Vector3(x, y, z).normalize()
    }

    companion object {
        fun identity(): Matrix4 {
            val m = Matrix4()
            m[0, 0] = 1f
            m[1, 1] = 1f
            m[2, 2] = 1f
            m[3, 3] = 1f
            return m
        }

        fun translation(x: Float, y: Float, z: Float): Matrix4 {
            val m = identity()
            m[0, 3] = x
            m[1, 3] = y
            m[2, 3] = z
            return m
        }

        fun scale(sx: Float, sy: Float, sz: Float): Matrix4 {
            val m = identity()
            m[0, 0] = sx
            m[1, 1] = sy
            m[2, 2] = sz
            return m
        }

        fun rotationX(radians: Float): Matrix4 {
            val m = identity()
            val c = cos(radians)
            val s = sin(radians)
            m[1, 1] = c
            m[1, 2] = -s
            m[2, 1] = s
            m[2, 2] = c
            return m
        }

        fun rotationY(radians: Float): Matrix4 {
            val m = identity()
            val c = cos(radians)
            val s = sin(radians)
            m[0, 0] = c
            m[0, 2] = s
            m[2, 0] = -s
            m[2, 2] = c
            return m
        }

        fun rotationZ(radians: Float): Matrix4 {
            val m = identity()
            val c = cos(radians)
            val s = sin(radians)
            m[0, 0] = c
            m[0, 1] = -s
            m[1, 0] = s
            m[1, 1] = c
            return m
        }

        fun perspective(fovRadians: Float, aspect: Float, near: Float, far: Float): Matrix4 {
            val m = Matrix4()
            val f = 1f / tan(fovRadians / 2f)
            m[0, 0] = f / aspect
            m[1, 1] = f
            m[2, 2] = (far + near) / (near - far)
            m[2, 3] = (2f * far * near) / (near - far)
            m[3, 2] = -1f
            m[3, 3] = 0f
            return m
        }
    }
}
