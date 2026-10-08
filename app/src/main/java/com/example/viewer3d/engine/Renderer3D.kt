package com.example.viewer3d.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.viewer3d.math.Matrix4
import com.example.viewer3d.math.Vector3
import com.example.viewer3d.model.DimensionGuide
import com.example.viewer3d.model.LightRay
import com.example.viewer3d.model.Mesh3D
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

data class ProjectedVertex(
    val screenPos: Offset,
    val depthZ: Float,
    val isVisible: Boolean
)

data class RenderFace(
    val p0: Offset,
    val p1: Offset,
    val p2: Offset,
    val depth: Float,
    val color: Color,
    val edgeColor: Color,
    val partId: String,
    val isSelected: Boolean
)

data class RenderEdge(
    val p0: Offset,
    val p1: Offset,
    val depth: Float,
    val color: Color,
    val strokeWidth: Float
)

data class RenderDimension(
    val label: String,
    val start: Offset,
    val end: Offset,
    val textPos: Offset
)

data class RenderLightRay(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val strokeWidth: Float,
    val label: String
)

data class RenderLabel(
    val text: String,
    val targetPartId: String,
    val screenPos: Offset,
    val depthZ: Float,
    val isSelected: Boolean
)

data class RenderScene(
    val faces: List<RenderFace>,
    val edges: List<RenderEdge>,
    val labels: List<RenderLabel>,
    val dimensions: List<RenderDimension>,
    val lightRays: List<RenderLightRay>
)

object Renderer3D {

    private val KEY_LIGHT_DIR = Vector3(0.5f, 0.8f, 0.4f).normalize()
    private const val NEAR_PLANE_Z = -0.25f

    fun render(
        mesh: Mesh3D,
        camera: Camera3D,
        viewportWidth: Float,
        viewportHeight: Float,
        state: ViewerState,
        labelsData: List<com.example.domain.ModelLabel>
    ): RenderScene {
        if (viewportWidth <= 10f || viewportHeight <= 10f) {
            return RenderScene(emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        }

        val aspect = (viewportWidth / viewportHeight).coerceIn(0.2f, 5.0f)
        val proj = Matrix4.perspective(camera.fovRadians, aspect, 0.1f, 100f)

        // View matrix: Rotate around X (pitch), Y (yaw), translate by pan and camera distance
        val rotX = Matrix4.rotationX(camera.pitchRadians)
        val rotY = Matrix4.rotationY(camera.yawRadians)
        val trans = Matrix4.translation(camera.panX, -camera.panY, -camera.distance)
        val viewMatrix = trans * (rotX * rotY)

        val halfW = viewportWidth / 2f
        val halfH = viewportHeight / 2f

        // Transform all vertices safely with near-plane and finite guards
        val projVertices = mesh.vertices.map { v ->
            val viewPt = viewMatrix.transformPoint(v)
            val inFrontOfCamera = viewPt.z < NEAR_PLANE_Z
            if (inFrontOfCamera) {
                val clipPt = proj.transformPoint(viewPt)
                val screenX = halfW + clipPt.x * halfW
                val screenY = halfH - clipPt.y * halfH
                val isFinite = screenX.isFinite() && screenY.isFinite() &&
                        screenX in -4000f..4000f && screenY in -4000f..4000f
                ProjectedVertex(
                    screenPos = if (isFinite) Offset(screenX, screenY) else Offset.Zero,
                    depthZ = viewPt.z,
                    isVisible = isFinite
                )
            } else {
                ProjectedVertex(
                    screenPos = Offset.Zero,
                    depthZ = viewPt.z,
                    isVisible = false
                )
            }
        }

        val isWireframe = state.shadingMode == ShadingMode.TECHNICAL_WIREFRAME
        val isCutaway = state.shadingMode == ShadingMode.CUTAWAY_XRAY

        val renderFaces = mutableListOf<RenderFace>()
        val renderEdges = mutableListOf<RenderEdge>()

        mesh.subMeshes.forEach { subMesh ->
            val isPartSelected = (state.selectedPartId == subMesh.partId)
            val baseColorLong = subMesh.defaultColor
            val baseR = ((baseColorLong shr 16) and 0xFF).toFloat() / 255f
            val baseG = ((baseColorLong shr 8) and 0xFF).toFloat() / 255f
            val baseB = (baseColorLong and 0xFF).toFloat() / 255f

            subMesh.faces.forEach { face ->
                val pv0 = projVertices.getOrNull(face.a) ?: return@forEach
                val pv1 = projVertices.getOrNull(face.b) ?: return@forEach
                val pv2 = projVertices.getOrNull(face.c) ?: return@forEach

                // CULL any face where ANY vertex is behind near plane or non-finite
                if (!pv0.isVisible || !pv1.isVisible || !pv2.isVisible) return@forEach

                // Backface culling in screen space (signed area)
                val ax = pv1.screenPos.x - pv0.screenPos.x
                val ay = pv1.screenPos.y - pv0.screenPos.y
                val bx = pv2.screenPos.x - pv0.screenPos.x
                val by = pv2.screenPos.y - pv0.screenPos.y
                val crossZ = ax * by - ay * bx

                // Cull backfaces unless wireframe or cutaway
                if (!isCutaway && !isWireframe && crossZ >= 0f) {
                    return@forEach
                }

                val avgDepth = (pv0.depthZ + pv1.depthZ + pv2.depthZ) / 3f

                // Normal transformed to view space
                val viewNormal = (rotX * rotY).transformDirection(face.normal)
                val diffuse = max(0f, viewNormal.dot(KEY_LIGHT_DIR))
                val ambient = 0.38f
                val lightIntensity = (ambient + diffuse * 0.62f).coerceIn(0.15f, 1.0f)

                // Specular highlight
                val viewDir = Vector3(0f, 0f, 1f)
                val halfVector = (KEY_LIGHT_DIR + viewDir).normalize()
                val spec = max(0f, viewNormal.dot(halfVector)).pow(16) * 0.28f

                val finalR = (baseR * lightIntensity + spec).coerceIn(0f, 1f)
                val finalG = (baseG * lightIntensity + spec).coerceIn(0f, 1f)
                val finalB = (baseB * lightIntensity + spec).coerceIn(0f, 1f)

                val alpha = when {
                    isCutaway -> if (isPartSelected) 0.9f else 0.35f
                    isWireframe -> 0.08f
                    isPartSelected -> 1.0f
                    else -> 0.95f
                }

                val faceColor = if (isPartSelected) {
                    Color(0xFF00E5FF).copy(alpha = if (isCutaway) 0.85f else 1.0f)
                } else {
                    Color(finalR, finalG, finalB, alpha)
                }

                val edgeColor = if (isPartSelected) {
                    Color(0xFF00E5FF)
                } else if (isWireframe) {
                    Color(0xFF38BDF8).copy(alpha = 0.85f)
                } else {
                    Color(finalR * 0.7f, finalG * 0.7f, finalB * 0.7f, 0.45f)
                }

                renderFaces.add(
                    RenderFace(
                        p0 = pv0.screenPos,
                        p1 = pv1.screenPos,
                        p2 = pv2.screenPos,
                        depth = avgDepth,
                        color = faceColor,
                        edgeColor = edgeColor,
                        partId = face.partId,
                        isSelected = isPartSelected
                    )
                )
            }

            // Edges (for wireframe or explicit boundaries)
            subMesh.edges.forEach { edge ->
                val v0 = projVertices.getOrNull(edge.a) ?: return@forEach
                val v1 = projVertices.getOrNull(edge.b) ?: return@forEach
                if (!v0.isVisible || !v1.isVisible) return@forEach

                val avgDepth = (v0.depthZ + v1.depthZ) / 2f
                val edgeColor = if (isPartSelected) Color(0xFF00E5FF) else Color(0xFF38BDF8)
                renderEdges.add(
                    RenderEdge(
                        p0 = v0.screenPos,
                        p1 = v1.screenPos,
                        depth = avgDepth,
                        color = edgeColor,
                        strokeWidth = if (isPartSelected) 3.5f else 1.8f
                    )
                )
            }
        }

        // Sort faces from back to front (Painter's algorithm: most negative Z is farthest from camera)
        renderFaces.sortBy { it.depth }

        // Projected Dimensions (with strict near-plane safety checks)
        val renderDimensions = mutableListOf<RenderDimension>()
        if (state.showDimensions) {
            mesh.dimensionGuides.forEach { guide ->
                val startView = viewMatrix.transformPoint(guide.start)
                val endView = viewMatrix.transformPoint(guide.end)
                if (startView.z < NEAR_PLANE_Z && endView.z < NEAR_PLANE_Z) {
                    val startClip = proj.transformPoint(startView)
                    val endClip = proj.transformPoint(endView)
                    val s0 = Offset(halfW + startClip.x * halfW, halfH - startClip.y * halfH)
                    val s1 = Offset(halfW + endClip.x * halfW, halfH - endClip.y * halfH)
                    if (s0.x.isFinite() && s0.y.isFinite() && s1.x.isFinite() && s1.y.isFinite()) {
                        val mid = Offset((s0.x + s1.x) / 2f, (s0.y + s1.y) / 2f - 18f)
                        if (mid.x in 10f..(viewportWidth - 10f) && mid.y in 10f..(viewportHeight - 10f)) {
                            renderDimensions.add(RenderDimension(guide.label, s0, s1, mid))
                        }
                    }
                }
            }
        }

        // Projected Light Rays
        val renderLightRays = mutableListOf<RenderLightRay>()
        mesh.lightRays.forEach { ray ->
            val sView = viewMatrix.transformPoint(ray.start)
            val eView = viewMatrix.transformPoint(ray.end)
            if (sView.z < NEAR_PLANE_Z && eView.z < NEAR_PLANE_Z) {
                val sClip = proj.transformPoint(sView)
                val eClip = proj.transformPoint(eView)
                val s0 = Offset(halfW + sClip.x * halfW, halfH - sClip.y * halfH)
                val s1 = Offset(halfW + eClip.x * halfW, halfH - eClip.y * halfH)
                if (s0.x.isFinite() && s0.y.isFinite() && s1.x.isFinite() && s1.y.isFinite()) {
                    renderLightRays.add(RenderLightRay(s0, s1, Color(ray.color), ray.width, ray.label))
                }
            }
        }

        // Projected 3D Labels (with near plane and viewport safety checks)
        val renderLabels = mutableListOf<RenderLabel>()
        if (state.showLabels) {
            labelsData.forEach { lbl ->
                val anchor = Vector3(lbl.anchorX, lbl.anchorY, lbl.anchorZ)
                val vView = viewMatrix.transformPoint(anchor)
                if (vView.z < NEAR_PLANE_Z) {
                    val vClip = proj.transformPoint(vView)
                    val screenX = halfW + vClip.x * halfW
                    val screenY = halfH - vClip.y * halfH
                    if (screenX.isFinite() && screenY.isFinite() &&
                        screenX in 10f..(viewportWidth - 10f) &&
                        screenY in 10f..(viewportHeight - 10f)
                    ) {
                        val isSelected = (state.selectedPartId == lbl.targetPartId)
                        renderLabels.add(
                            RenderLabel(
                                text = lbl.text,
                                targetPartId = lbl.targetPartId,
                                screenPos = Offset(screenX, screenY),
                                depthZ = vView.z,
                                isSelected = isSelected
                            )
                        )
                    }
                }
            }
        }

        return RenderScene(
            faces = renderFaces,
            edges = renderEdges,
            labels = renderLabels,
            dimensions = renderDimensions,
            lightRays = renderLightRays
        )
    }

    fun hitTestPart(
        tapPos: Offset,
        scene: RenderScene
    ): String? {
        if (!tapPos.x.isFinite() || !tapPos.y.isFinite()) return null

        // Find tapped label first
        val tappedLabel = scene.labels.find { lbl ->
            lbl.screenPos.x.isFinite() && lbl.screenPos.y.isFinite() &&
                    (lbl.screenPos - tapPos).getDistance() <= 40f
        }
        if (tappedLabel != null) return tappedLabel.targetPartId

        // Hit test faces from front to back
        for (i in scene.faces.indices.reversed()) {
            val face = scene.faces[i]
            if (isPointInTriangle(tapPos, face.p0, face.p1, face.p2)) {
                return face.partId
            }
        }
        return null
    }

    private fun isPointInTriangle(p: Offset, a: Offset, b: Offset, c: Offset): Boolean {
        if (!p.x.isFinite() || !p.y.isFinite() ||
            !a.x.isFinite() || !a.y.isFinite() ||
            !b.x.isFinite() || !b.y.isFinite() ||
            !c.x.isFinite() || !c.y.isFinite()
        ) return false

        val d1 = sign(p, a, b)
        val d2 = sign(p, b, c)
        val d3 = sign(p, c, a)
        if (d1.isNaN() || d2.isNaN() || d3.isNaN()) return false

        val hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0)
        val hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0)
        return !(hasNeg && hasPos)
    }

    private fun sign(p1: Offset, p2: Offset, p3: Offset): Float {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y)
    }
}
