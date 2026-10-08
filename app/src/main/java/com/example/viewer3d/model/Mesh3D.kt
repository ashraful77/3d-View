package com.example.viewer3d.model

import com.example.viewer3d.math.Vector3

data class Face3D(
    val a: Int,
    val b: Int,
    val c: Int,
    val partId: String,
    val normal: Vector3,
    val customColor: Long? = null
)

data class Edge3D(
    val a: Int,
    val b: Int,
    val partId: String = ""
)

data class DimensionGuide(
    val label: String,
    val start: Vector3,
    val end: Vector3,
    val offsetDir: Vector3 = Vector3.UP
)

data class LightRay(
    val start: Vector3,
    val end: Vector3,
    val color: Long,
    val width: Float = 3f,
    val label: String = ""
)

data class SubMesh3D(
    val partId: String,
    val name: String,
    val defaultColor: Long,
    val faces: List<Face3D> = emptyList(),
    val edges: List<Edge3D> = emptyList()
)

data class Mesh3D(
    val vertices: List<Vector3>,
    val subMeshes: List<SubMesh3D>,
    val boundingRadius: Float = 2.0f,
    val dimensionGuides: List<DimensionGuide> = emptyList(),
    val lightRays: List<LightRay> = emptyList(),
    // Custom dynamic morphing closures:
    val netMorpher: ((Float) -> Mesh3D)? = null,
    val animationMorpher: ((Float) -> Mesh3D)? = null
) {
    val allFaces: List<Face3D> by lazy {
        subMeshes.flatMap { it.faces }
    }

    val allEdges: List<Edge3D> by lazy {
        subMeshes.flatMap { it.edges }
    }

    fun getPartColor(partId: String): Long {
        return subMeshes.find { it.partId == partId }?.defaultColor ?: 0xFF4A90E2
    }
}
