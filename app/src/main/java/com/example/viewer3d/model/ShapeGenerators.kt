package com.example.viewer3d.model

import com.example.viewer3d.math.Vector3
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object ShapeGenerators {

    // ==========================================
    // 1. CUBE (Mathematics - Solid Geometry)
    // ==========================================
    fun createCube(size: Float = 2.0f): Mesh3D {
        val s = size / 2f

        fun buildCubeAtT(t: Float): Mesh3D {
            // Net unfolding logic:
            // Base face (bottom) stays flat on y = -s.
            // Front, Back, Left, Right unfold outwards by 90° * t.
            // Top face unfolds attached to the Back face by an additional 90° * t!
            val angle = (PI.toFloat() / 2f) * t
            val cosA = cos(angle)
            val sinA = sin(angle)

            val vList = mutableListOf<Vector3>()
            val subMeshes = mutableListOf<SubMesh3D>()

            fun addQuad(
                p0: Vector3, p1: Vector3, p2: Vector3, p3: Vector3,
                partId: String, name: String, color: Long
            ) {
                val idx = vList.size
                vList.add(p0)
                vList.add(p1)
                vList.add(p2)
                vList.add(p3)
                val normal = (p1 - p0).cross(p2 - p0).normalize()
                val f1 = Face3D(idx, idx + 1, idx + 2, partId, normal)
                val f2 = Face3D(idx, idx + 2, idx + 3, partId, normal)
                val edges = listOf(
                    Edge3D(idx, idx + 1, partId),
                    Edge3D(idx + 1, idx + 2, partId),
                    Edge3D(idx + 2, idx + 3, partId),
                    Edge3D(idx + 3, idx, partId)
                )
                subMeshes.add(SubMesh3D(partId, name, color, listOf(f1, f2), edges))
            }

            // 1. Bottom Face (Anchor on ground plane y = -s)
            addQuad(
                Vector3(-s, -s, -s),
                Vector3(s, -s, -s),
                Vector3(s, -s, s),
                Vector3(-s, -s, s),
                "bottom_face", "Bottom Base Face", 0xFF38BDF8
            )

            // 2. Front Face (hinged at bottom front edge: z = s, y = -s)
            val f_v0 = Vector3(-s, -s, s)
            val f_v1 = Vector3(s, -s, s)
            val f_v2 = Vector3(s, -s + 2 * s * cosA, s + 2 * s * sinA)
            val f_v3 = Vector3(-s, -s + 2 * s * cosA, s + 2 * s * sinA)
            addQuad(f_v0, f_v1, f_v2, f_v3, "front_face", "Front Face", 0xFF0284C7)

            // 3. Back Face (hinged at bottom back edge: z = -s, y = -s)
            val b_v0 = Vector3(s, -s, -s)
            val b_v1 = Vector3(-s, -s, -s)
            val b_v2 = Vector3(-s, -s + 2 * s * cosA, -s - 2 * s * sinA)
            val b_v3 = Vector3(s, -s + 2 * s * cosA, -s - 2 * s * sinA)
            addQuad(b_v0, b_v1, b_v2, b_v3, "back_face", "Back Face", 0xFF0369A1)

            // 4. Left Face (hinged at bottom left edge: x = -s, y = -s)
            val l_v0 = Vector3(-s, -s, -s)
            val l_v1 = Vector3(-s, -s, s)
            val l_v2 = Vector3(-s - 2 * s * sinA, -s + 2 * s * cosA, s)
            val l_v3 = Vector3(-s - 2 * s * sinA, -s + 2 * s * cosA, -s)
            addQuad(l_v0, l_v1, l_v2, l_v3, "left_face", "Left Face", 0xFF0EA5E9)

            // 5. Right Face (hinged at bottom right edge: x = s, y = -s)
            val r_v0 = Vector3(s, -s, s)
            val r_v1 = Vector3(s, -s, -s)
            val r_v2 = Vector3(s + 2 * s * sinA, -s + 2 * s * cosA, -s)
            val r_v3 = Vector3(s + 2 * s * sinA, -s + 2 * s * cosA, s)
            addQuad(r_v0, r_v1, r_v2, r_v3, "right_face", "Right Face", 0xFF38BDF8)

            // 6. Top Face (hinged at top edge of back face: b_v2 and b_v3)
            // At t = 0: y = s, z extends from -s to s (parallel to bottom base).
            // At t = 1: unfolds further backwards to lie flat at y = -s, extending from -3s to -5s.
            val topAngle = 2f * angle
            val dy = -2f * s * sin(topAngle)
            val dz = 2f * s * cos(topAngle)
            val t_base_l = b_v2
            val t_base_r = b_v3
            val t_far_l = Vector3(-s, b_v2.y + dy, b_v2.z + dz)
            val t_far_r = Vector3(s, b_v3.y + dy, b_v3.z + dz)
            addQuad(t_base_l, t_far_l, t_far_r, t_base_r, "top_face", "Top Face", 0xFF7DD3FC)

            val dimensions = listOf(
                DimensionGuide("Side a = 5 cm", Vector3(-s, -s - 0.2f, s), Vector3(s, -s - 0.2f, s), Vector3(0f, -1f, 0f)),
                DimensionGuide("Height h = 5 cm", Vector3(s + 0.2f, -s, s), Vector3(s + 0.2f, s, s), Vector3(1f, 0f, 0f)),
                DimensionGuide("Depth d = 5 cm", Vector3(s, -s - 0.2f, -s), Vector3(s, -s - 0.2f, s), Vector3(0f, -1f, 0f))
            )

            return Mesh3D(
                vertices = vList,
                subMeshes = subMeshes,
                boundingRadius = 3.2f,
                dimensionGuides = dimensions,
                netMorpher = { tVal -> buildCubeAtT(tVal) }
            )
        }

        return buildCubeAtT(0f)
    }

    // ==========================================
    // 2. CYLINDER (Mathematics - Solid Geometry)
    // ==========================================
    fun createCylinder(radius: Float = 1.2f, height: Float = 2.4f, segments: Int = 28): Mesh3D {
        val halfH = height / 2f

        fun buildCylinderAtT(t: Float): Mesh3D {
            val vList = mutableListOf<Vector3>()
            val subMeshes = mutableListOf<SubMesh3D>()

            val unrollAngle = (1f - t) * 2f * PI.toFloat()
            val arcLength = 2f * PI.toFloat() * radius

            // Curved lateral mantle
            val mantleFaces = mutableListOf<Face3D>()
            val mantleEdges = mutableListOf<Edge3D>()

            for (i in 0 until segments) {
                val frac0 = i.toFloat() / segments
                val frac1 = (i + 1).toFloat() / segments

                val p0: Vector3
                val p1: Vector3
                val p2: Vector3
                val p3: Vector3

                if (t < 0.05f) {
                    // Fully 3D cylindrical
                    val th0 = frac0 * 2f * PI.toFloat()
                    val th1 = frac1 * 2f * PI.toFloat()
                    p0 = Vector3(radius * cos(th0), -halfH, radius * sin(th0))
                    p1 = Vector3(radius * cos(th1), -halfH, radius * sin(th1))
                    p2 = Vector3(radius * cos(th1), halfH, radius * sin(th1))
                    p3 = Vector3(radius * cos(th0), halfH, radius * sin(th0))
                } else {
                    // Interpolated unrolling into flat planar rectangle of width 2πr
                    val flatX0 = (frac0 - 0.5f) * arcLength
                    val flatX1 = (frac1 - 0.5f) * arcLength

                    val th0 = frac0 * unrollAngle
                    val th1 = frac1 * unrollAngle

                    val cylP0 = Vector3(radius * cos(th0), -halfH, radius * sin(th0))
                    val cylP1 = Vector3(radius * cos(th1), -halfH, radius * sin(th1))
                    val cylP2 = Vector3(radius * cos(th1), halfH, radius * sin(th1))
                    val cylP3 = Vector3(radius * cos(th0), halfH, radius * sin(th0))

                    val flatP0 = Vector3(flatX0, -halfH, 0f)
                    val flatP1 = Vector3(flatX1, -halfH, 0f)
                    val flatP2 = Vector3(flatX1, halfH, 0f)
                    val flatP3 = Vector3(flatX0, halfH, 0f)

                    p0 = Vector3.lerp(cylP0, flatP0, t)
                    p1 = Vector3.lerp(cylP1, flatP1, t)
                    p2 = Vector3.lerp(cylP2, flatP2, t)
                    p3 = Vector3.lerp(cylP3, flatP3, t)
                }

                val idx = vList.size
                vList.addAll(listOf(p0, p1, p2, p3))
                val normal = (p1 - p0).cross(p2 - p0).normalize()
                mantleFaces.add(Face3D(idx, idx + 1, idx + 2, "curved_surface", normal))
                mantleFaces.add(Face3D(idx, idx + 2, idx + 3, "curved_surface", normal))
                mantleEdges.add(Edge3D(idx, idx + 1, "curved_surface"))
                mantleEdges.add(Edge3D(idx + 2, idx + 3, "curved_surface"))
            }

            subMeshes.add(SubMesh3D("curved_surface", "Curved Lateral Surface", 0xFF0284C7, mantleFaces, mantleEdges))

            // Top Circular Base
            val topCenterIdx = vList.size
            val topOffset = if (t > 0f) Vector3(0f, halfH + t * radius, 0f) else Vector3(0f, halfH, 0f)
            vList.add(topOffset)
            val topFaces = mutableListOf<Face3D>()
            val topEdges = mutableListOf<Edge3D>()

            val topPerimStart = vList.size
            for (i in 0 until segments) {
                val th = (i.toFloat() / segments) * 2f * PI.toFloat()
                val pt = if (t > 0f) {
                    Vector3(radius * cos(th), halfH + t * radius, radius * sin(th) * (1f - t))
                } else {
                    Vector3(radius * cos(th), halfH, radius * sin(th))
                }
                vList.add(pt)
            }

            for (i in 0 until segments) {
                val next = (i + 1) % segments
                topFaces.add(Face3D(topCenterIdx, topPerimStart + i, topPerimStart + next, "top_base", Vector3.UP))
                topEdges.add(Edge3D(topPerimStart + i, topPerimStart + next, "top_base"))
            }
            subMeshes.add(SubMesh3D("top_base", "Top Circular Base", 0xFF38BDF8, topFaces, topEdges))

            // Bottom Circular Base
            val botCenterIdx = vList.size
            val botOffset = if (t > 0f) Vector3(0f, -halfH - t * radius, 0f) else Vector3(0f, -halfH, 0f)
            vList.add(botOffset)
            val botFaces = mutableListOf<Face3D>()
            val botEdges = mutableListOf<Edge3D>()

            val botPerimStart = vList.size
            for (i in 0 until segments) {
                val th = (i.toFloat() / segments) * 2f * PI.toFloat()
                val pt = if (t > 0f) {
                    Vector3(radius * cos(th), -halfH - t * radius, radius * sin(th) * (1f - t))
                } else {
                    Vector3(radius * cos(th), -halfH, radius * sin(th))
                }
                vList.add(pt)
            }

            for (i in 0 until segments) {
                val next = (i + 1) % segments
                botFaces.add(Face3D(botCenterIdx, botPerimStart + next, botPerimStart + i, "bottom_base", Vector3(0f, -1f, 0f)))
                botEdges.add(Edge3D(botPerimStart + i, botPerimStart + next, "bottom_base"))
            }
            subMeshes.add(SubMesh3D("bottom_base", "Bottom Circular Base", 0xFF0369A1, botFaces, botEdges))

            val dimensions = listOf(
                DimensionGuide("Radius r = 4 cm", Vector3(0f, halfH, 0f), Vector3(radius, halfH, 0f), Vector3.UP),
                DimensionGuide("Height h = 10 cm", Vector3(-radius - 0.3f, -halfH, 0f), Vector3(-radius - 0.3f, halfH, 0f), Vector3(-1f, 0f, 0f)),
                DimensionGuide("Circumference C = 2πr", Vector3(0f, -halfH - 0.2f, -radius), Vector3(0f, -halfH - 0.2f, radius), Vector3(0f, 0f, 1f))
            )

            return Mesh3D(
                vertices = vList,
                subMeshes = subMeshes,
                boundingRadius = 3.5f,
                dimensionGuides = dimensions,
                netMorpher = { tVal -> buildCylinderAtT(tVal) }
            )
        }

        return buildCylinderAtT(0f)
    }

    // ==========================================
    // 3. CONE (Mathematics - Solid Geometry)
    // ==========================================
    fun createCone(radius: Float = 1.3f, height: Float = 2.4f, segments: Int = 28): Mesh3D {
        val halfH = height / 2f
        val slantHeight = sqrt(radius * radius + height * height)

        fun buildConeAtT(t: Float): Mesh3D {
            val vList = mutableListOf<Vector3>()
            val subMeshes = mutableListOf<SubMesh3D>()

            val apexPos = if (t > 0f) {
                Vector3(0f, halfH * (1f - t) + t * (slantHeight / 2f), 0f)
            } else {
                Vector3(0f, halfH, 0f)
            }
            val apexIdx = vList.size
            vList.add(apexPos)

            val basePerimStart = vList.size
            val mantleFaces = mutableListOf<Face3D>()
            val mantleEdges = mutableListOf<Edge3D>()

            val sectorSpread = 2f * PI.toFloat() * (radius / slantHeight)

            for (i in 0 until segments) {
                val frac = i.toFloat() / segments
                val pt: Vector3
                if (t < 0.05f) {
                    val th = frac * 2f * PI.toFloat()
                    pt = Vector3(radius * cos(th), -halfH, radius * sin(th))
                } else {
                    val th3D = frac * 2f * PI.toFloat()
                    val p3D = Vector3(radius * cos(th3D), -halfH, radius * sin(th3D))

                    val sectorTh = (frac - 0.5f) * sectorSpread
                    val p2D = Vector3(slantHeight * sin(sectorTh), apexPos.y - slantHeight * cos(sectorTh), 0f)

                    pt = Vector3.lerp(p3D, p2D, t)
                }
                vList.add(pt)
            }

            for (i in 0 until segments) {
                val next = (i + 1) % segments
                val v1 = vList[basePerimStart + i]
                val v2 = vList[basePerimStart + next]
                val normal = (v2 - apexPos).cross(v1 - apexPos).normalize()
                mantleFaces.add(Face3D(apexIdx, basePerimStart + i, basePerimStart + next, "lateral_surface", normal))
                mantleEdges.add(Edge3D(apexIdx, basePerimStart + i, "lateral_surface"))
                mantleEdges.add(Edge3D(basePerimStart + i, basePerimStart + next, "lateral_surface"))
            }

            subMeshes.add(SubMesh3D("lateral_surface", "Conical Lateral Surface", 0xFF0EA5E9, mantleFaces, mantleEdges))

            // Base disk
            val baseCenterIdx = vList.size
            val baseCenterPos = if (t > 0f) Vector3(0f, -halfH - t * radius * 1.5f, 0f) else Vector3(0f, -halfH, 0f)
            vList.add(baseCenterPos)
            val baseFaces = mutableListOf<Face3D>()
            val baseEdges = mutableListOf<Edge3D>()

            val diskPerimStart = vList.size
            for (i in 0 until segments) {
                val th = (i.toFloat() / segments) * 2f * PI.toFloat()
                val pt = if (t > 0f) {
                    Vector3(radius * cos(th), baseCenterPos.y, radius * sin(th) * (1f - t))
                } else {
                    Vector3(radius * cos(th), -halfH, radius * sin(th))
                }
                vList.add(pt)
            }

            for (i in 0 until segments) {
                val next = (i + 1) % segments
                baseFaces.add(Face3D(baseCenterIdx, diskPerimStart + next, diskPerimStart + i, "base_disk", Vector3(0f, -1f, 0f)))
                baseEdges.add(Edge3D(diskPerimStart + i, diskPerimStart + next, "base_disk"))
            }
            subMeshes.add(SubMesh3D("base_disk", "Circular Base Disk", 0xFF0284C7, baseFaces, baseEdges))

            val dimensions = listOf(
                DimensionGuide("Radius r = 3 cm", Vector3(0f, -halfH, 0f), Vector3(radius, -halfH, 0f), Vector3(0f, -1f, 0f)),
                DimensionGuide("Vertical Height h = 7 cm", Vector3(0f, -halfH, 0f), Vector3(0f, halfH, 0f), Vector3(-1f, 0f, 0f)),
                DimensionGuide("Slant Height l ≈ 7.6 cm", Vector3(radius, -halfH, 0f), Vector3(0f, halfH, 0f), Vector3(1f, 1f, 0f).normalize())
            )

            return Mesh3D(
                vertices = vList,
                subMeshes = subMeshes,
                boundingRadius = 3.5f,
                dimensionGuides = dimensions,
                netMorpher = { tVal -> buildConeAtT(tVal) }
            )
        }

        return buildConeAtT(0f)
    }

    // ==========================================
    // 4. SPHERE (Mathematics - Solid Geometry)
    // ==========================================
    fun createSphere(radius: Float = 1.4f, latBands: Int = 18, lonBands: Int = 24): Mesh3D {
        val vList = mutableListOf<Vector3>()
        val northFaces = mutableListOf<Face3D>()
        val southFaces = mutableListOf<Face3D>()
        val equatorEdges = mutableListOf<Edge3D>()

        for (lat in 0..latBands) {
            val theta = (lat.toFloat() / latBands) * PI.toFloat()
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)

            for (lon in 0..lonBands) {
                val phi = (lon.toFloat() / lonBands) * 2f * PI.toFloat()
                val sinPhi = sin(phi)
                val cosPhi = cos(phi)

                val x = radius * cosPhi * sinTheta
                val y = radius * cosTheta
                val z = radius * sinPhi * sinTheta
                vList.add(Vector3(x, y, z))
            }
        }

        val stride = lonBands + 1
        val midLat = latBands / 2

        for (lat in 0 until latBands) {
            for (lon in 0 until lonBands) {
                val first = lat * stride + lon
                val second = first + stride

                val normal = vList[first].normalize()
                val isNorth = lat < midLat
                val targetList = if (isNorth) northFaces else southFaces
                val partId = if (isNorth) "northern_hemisphere" else "southern_hemisphere"

                targetList.add(Face3D(first, second, first + 1, partId, normal))
                targetList.add(Face3D(second, second + 1, first + 1, partId, normal))

                if (lat == midLat) {
                    equatorEdges.add(Edge3D(first, first + 1, "equator_circle"))
                }
            }
        }

        val subMeshes = listOf(
            SubMesh3D("northern_hemisphere", "Northern Hemisphere", 0xFF38BDF8, northFaces),
            SubMesh3D("southern_hemisphere", "Southern Hemisphere", 0xFF0284C7, southFaces),
            SubMesh3D("equator_circle", "Great Circle / Equator", 0xFFF59E0B, emptyList(), equatorEdges)
        )

        val dimensions = listOf(
            DimensionGuide("Radius r = 4 cm", Vector3.ZERO, Vector3(radius, 0f, 0f), Vector3.UP),
            DimensionGuide("Diameter d = 8 cm", Vector3(-radius, 0f, 0f), Vector3(radius, 0f, 0f), Vector3(0f, -1f, 0f)),
            DimensionGuide("Great Circle C = 2πr", Vector3(0f, 0f, -radius), Vector3(0f, 0f, radius), Vector3(1f, 0f, 0f))
        )

        return Mesh3D(
            vertices = vList,
            subMeshes = subMeshes,
            boundingRadius = 2.4f,
            dimensionGuides = dimensions
        )
    }

    // ==========================================
    // 5. HUMAN HEART (Biology - Human Anatomy)
    // ==========================================
    fun createHumanHeart(): Mesh3D {
        fun buildHeartAtPhase(phase: Float): Mesh3D {
            // Realistic cardiac cycle:
            // Systole pulse (0.0 to 0.4): Ventricles contract strongly (scale down), atria expand
            // Diastole relaxation (0.4 to 1.0): Ventricles refill, atria relax
            val beatFactor = if (phase < 0.35f) {
                val t = phase / 0.35f
                1f - 0.14f * sin(t * PI.toFloat())
            } else {
                val t = (phase - 0.35f) / 0.65f
                1f + 0.04f * sin(t * PI.toFloat())
            }

            val vList = mutableListOf<Vector3>()
            val subMeshes = mutableListOf<SubMesh3D>()

            // Helper to build a curved ellipsoid anatomical organ part
            fun addOrganChamber(
                center: Vector3,
                radii: Vector3,
                rotationEuler: Vector3,
                partId: String,
                name: String,
                color: Long,
                pulseScale: Float = 1.0f,
                clipCutaway: Boolean = false
            ) {
                val startIdx = vList.size
                val latSteps = 12
                val lonSteps = 16
                val faces = mutableListOf<Face3D>()
                val edges = mutableListOf<Edge3D>()

                val rx = radii.x * pulseScale
                val ry = radii.y * pulseScale
                val rz = radii.z * pulseScale

                for (lat in 0..latSteps) {
                    val th = (lat.toFloat() / latSteps) * PI.toFloat()
                    val sinTh = sin(th)
                    val cosTh = cos(th)

                    for (lon in 0..lonSteps) {
                        val phi = (lon.toFloat() / lonSteps) * 2f * PI.toFloat()
                        var px = rx * cos(phi) * sinTh
                        var py = ry * cosTh
                        var pz = rz * sin(phi) * sinTh

                        // Euler rotations
                        val cX = cos(rotationEuler.x); val sX = sin(rotationEuler.x)
                        val y1 = py * cX - pz * sX
                        val z1 = py * sX + pz * cX

                        val cY = cos(rotationEuler.y); val sY = sin(rotationEuler.y)
                        val x2 = px * cY + z1 * sY
                        val z2 = -px * sY + z1 * cY

                        val cZ = cos(rotationEuler.z); val sZ = sin(rotationEuler.z)
                        val x3 = x2 * cZ - y1 * sZ
                        val y3 = x2 * sZ + y1 * cZ

                        val worldPt = Vector3(x3 + center.x, y3 + center.y, z2 + center.z)
                        vList.add(worldPt)
                    }
                }

                val stride = lonSteps + 1
                for (lat in 0 until latSteps) {
                    for (lon in 0 until lonSteps) {
                        val first = startIdx + lat * stride + lon
                        val second = first + stride

                        val n = (vList[first] - center).normalize()
                        faces.add(Face3D(first, second, first + 1, partId, n))
                        faces.add(Face3D(second, second + 1, first + 1, partId, n))
                    }
                }

                subMeshes.add(SubMesh3D(partId, name, color, faces, edges))
            }

            // Helper to build curved tubular vessels (Aorta Arch, Pulmonary Trunk, Vena Cava)
            fun addTubularVessel(
                pathPoints: List<Vector3>,
                radius: Float,
                partId: String,
                name: String,
                color: Long
            ) {
                val startIdx = vList.size
                val ringSegments = 12
                val faces = mutableListOf<Face3D>()
                val edges = mutableListOf<Edge3D>()

                for (p in pathPoints) {
                    for (i in 0 until ringSegments) {
                        val angle = (i.toFloat() / ringSegments) * 2f * PI.toFloat()
                        val ringPt = Vector3(
                            p.x + radius * cos(angle),
                            p.y,
                            p.z + radius * sin(angle)
                        )
                        vList.add(ringPt)
                    }
                }

                for (ring in 0 until pathPoints.size - 1) {
                    val r0 = startIdx + ring * ringSegments
                    val r1 = startIdx + (ring + 1) * ringSegments
                    for (i in 0 until ringSegments) {
                        val next = (i + 1) % ringSegments
                        val p0 = r0 + i
                        val p1 = r0 + next
                        val p2 = r1 + next
                        val p3 = r1 + i

                        val n = (vList[p0] - pathPoints[ring]).normalize()
                        faces.add(Face3D(p0, p1, p2, partId, n))
                        faces.add(Face3D(p0, p2, p3, partId, n))
                        edges.add(Edge3D(p0, p1, partId))
                    }
                }

                subMeshes.add(SubMesh3D(partId, name, color, faces, edges))
            }

            // 1. LEFT VENTRICLE (Thick cardiac apex chamber, oxygenated, arterial scarlet red)
            addOrganChamber(
                center = Vector3(0.35f, -0.45f, 0.05f),
                radii = Vector3(0.65f, 0.95f, 0.65f),
                rotationEuler = Vector3(0.2f, 0f, -0.35f),
                partId = "left_ventricle",
                name = "Left Ventricle",
                color = 0xFFDC2626, // Crimson Red
                pulseScale = beatFactor
            )

            // 2. RIGHT VENTRICLE (Anterior chamber, sends deox blood to lungs, venous dark blue/crimson)
            addOrganChamber(
                center = Vector3(-0.45f, -0.35f, 0.15f),
                radii = Vector3(0.6f, 0.85f, 0.6f),
                rotationEuler = Vector3(0.15f, 0f, 0.3f),
                partId = "right_ventricle",
                name = "Right Ventricle",
                color = 0xFFB91C1C,
                pulseScale = beatFactor
            )

            // 3. LEFT ATRIUM (Upper posterior left, receives oxygenated blood from pulmonary veins)
            addOrganChamber(
                center = Vector3(0.4f, 0.5f, -0.15f),
                radii = Vector3(0.48f, 0.52f, 0.48f),
                rotationEuler = Vector3(0f, 0f, 0f),
                partId = "left_atrium",
                name = "Left Atrium",
                color = 0xFFE11D48,
                pulseScale = 2f - beatFactor // opposite phase pulse
            )

            // 4. RIGHT ATRIUM (Upper right anterior, receives systemic deoxygenated blood)
            addOrganChamber(
                center = Vector3(-0.55f, 0.45f, 0.05f),
                radii = Vector3(0.52f, 0.55f, 0.52f),
                rotationEuler = Vector3(0f, 0f, 0f),
                partId = "right_atrium",
                name = "Right Atrium",
                color = 0xFF1E40AF, // Deep Venous Blue
                pulseScale = 2f - beatFactor
            )

            // 5. AORTA (Grand arched main arterial artery feeding systemic circulation)
            val aortaPath = listOf(
                Vector3(0.05f, 0.2f, 0.05f),
                Vector3(0.05f, 0.7f, 0.0f),
                Vector3(0.0f, 1.15f, -0.1f),
                Vector3(-0.25f, 1.25f, -0.18f),
                Vector3(-0.55f, 1.1f, -0.25f),
                Vector3(-0.65f, 0.6f, -0.28f)
            )
            addTubularVessel(aortaPath, 0.24f, "aorta", "Aortic Arch", 0xFFEF4444)

            // 6. PULMONARY ARTERY (Crosses anteriorly over aorta toward left and right lungs)
            val pulmonaryPath = listOf(
                Vector3(-0.2f, 0.25f, 0.2f),
                Vector3(-0.1f, 0.65f, 0.15f),
                Vector3(0.2f, 0.9f, 0.05f),
                Vector3(0.55f, 0.95f, -0.1f)
            )
            addTubularVessel(pulmonaryPath, 0.22f, "pulmonary_artery", "Pulmonary Artery", 0xFF2563EB)

            // 7. SUPERIOR VENA CAVA (Large venous vessel entering right atrium)
            val venaCavaPath = listOf(
                Vector3(-0.58f, 1.2f, -0.05f),
                Vector3(-0.58f, 0.85f, -0.05f),
                Vector3(-0.58f, 0.55f, 0.0f)
            )
            addTubularVessel(venaCavaPath, 0.20f, "superior_vena_cava", "Superior Vena Cava", 0xFF1D4ED8)

            // 8. INTERVENTRICULAR SEPTUM (Central muscular wall separating left & right ventricles)
            addOrganChamber(
                center = Vector3(-0.05f, -0.4f, 0.05f),
                radii = Vector3(0.22f, 0.8f, 0.5f),
                rotationEuler = Vector3(0.18f, 0f, 0f),
                partId = "septum",
                name = "Interventricular Septum",
                color = 0xFF991B1B
            )

            // Blood Flow Demonstration Light Rays (Animated particles/arrows showing circulation direction)
            val flowRays = listOf(
                LightRay(Vector3(-0.58f, 1.1f, -0.05f), Vector3(-0.55f, 0.5f, 0.05f), 0xFF3B82F6, 4f, "Deoxygenated Systemic Inflow"),
                LightRay(Vector3(-0.45f, -0.25f, 0.15f), Vector3(0.5f, 0.95f, -0.1f), 0xFF60A5FA, 4f, "To Lungs via Pulmonary"),
                LightRay(Vector3(0.35f, -0.3f, 0.05f), Vector3(0.05f, 0.8f, 0.0f), 0xFFF87171, 4f, "Oxygenated Outflow via Aorta")
            )

            val dimensions = listOf(
                DimensionGuide("Cardiac Axis ~45°", Vector3(-0.6f, 0.7f, 0f), Vector3(0.5f, -0.9f, 0.1f), Vector3(1f, 1f, 0f).normalize()),
                DimensionGuide("Average Adult Size ~12 cm", Vector3(-0.8f, 0.8f, 0f), Vector3(-0.8f, -0.8f, 0f), Vector3(-1f, 0f, 0f))
            )

            return Mesh3D(
                vertices = vList,
                subMeshes = subMeshes,
                boundingRadius = 3.2f,
                dimensionGuides = dimensions,
                lightRays = flowRays,
                animationMorpher = { p -> buildHeartAtPhase(p) }
            )
        }

        return buildHeartAtPhase(0f)
    }

    // ==========================================
    // 6. WATER MOLECULE (Chemistry - Molecular Structure)
    // ==========================================
    fun createWaterMolecule(): Mesh3D {
        val vList = mutableListOf<Vector3>()
        val subMeshes = mutableListOf<SubMesh3D>()

        fun addSphere(center: Vector3, radius: Float, partId: String, name: String, color: Long) {
            val startIdx = vList.size
            val latSteps = 12
            val lonSteps = 16
            val faces = mutableListOf<Face3D>()

            for (lat in 0..latSteps) {
                val th = (lat.toFloat() / latSteps) * PI.toFloat()
                for (lon in 0..lonSteps) {
                    val phi = (lon.toFloat() / lonSteps) * 2f * PI.toFloat()
                    val pt = Vector3(
                        center.x + radius * cos(phi) * sin(th),
                        center.y + radius * cos(th),
                        center.z + radius * sin(phi) * sin(th)
                    )
                    vList.add(pt)
                }
            }

            val stride = lonSteps + 1
            for (lat in 0 until latSteps) {
                for (lon in 0 until lonSteps) {
                    val first = startIdx + lat * stride + lon
                    val second = first + stride
                    val n = (vList[first] - center).normalize()
                    faces.add(Face3D(first, second, first + 1, partId, n))
                    faces.add(Face3D(second, second + 1, first + 1, partId, n))
                }
            }
            subMeshes.add(SubMesh3D(partId, name, color, faces))
        }

        fun addCylinderBond(start: Vector3, end: Vector3, radius: Float, partId: String, name: String, color: Long) {
            val startIdx = vList.size
            val segments = 10
            val faces = mutableListOf<Face3D>()
            val dir = (end - start).normalize()
            val up = if (dir.x.let { kotlin.math.abs(it) } > 0.9f) Vector3.UP else Vector3.RIGHT
            val right = dir.cross(up).normalize()
            val perp = right.cross(dir).normalize()

            for (i in 0 until segments) {
                val a = (i.toFloat() / segments) * 2f * PI.toFloat()
                val offset = (right * cos(a) + perp * sin(a)) * radius
                vList.add(start + offset)
                vList.add(end + offset)
            }

            for (i in 0 until segments) {
                val next = (i + 1) % segments
                val p0 = startIdx + i * 2
                val p1 = startIdx + i * 2 + 1
                val p2 = startIdx + next * 2 + 1
                val p3 = startIdx + next * 2
                val n = (vList[p0] - start).normalize()
                faces.add(Face3D(p0, p1, p2, partId, n))
                faces.add(Face3D(p0, p2, p3, partId, n))
            }
            subMeshes.add(SubMesh3D(partId, name, color, faces))
        }

        // Oxygen Atom (Red, electronegative, larger radius 0.85)
        val oPos = Vector3(0f, 0.3f, 0f)
        addSphere(oPos, 0.85f, "oxygen_atom", "Oxygen Atom (O)", 0xFFEF4444)

        // Tetrahedral bond angle = 104.5 degrees (half angle = 52.25 degrees)
        val bondDist = 1.45f
        val halfAngleRad = (104.5f / 2f) * (PI.toFloat() / 180f)
        val h1Pos = Vector3(-bondDist * sin(halfAngleRad), oPos.y - bondDist * cos(halfAngleRad), 0f)
        val h2Pos = Vector3(bondDist * sin(halfAngleRad), oPos.y - bondDist * cos(halfAngleRad), 0f)

        // Hydrogen Atoms (White/light gray, smaller radius 0.45)
        addSphere(h1Pos, 0.45f, "hydrogen_1", "Hydrogen Atom 1 (H)", 0xFFF1F5F9)
        addSphere(h2Pos, 0.45f, "hydrogen_2", "Hydrogen Atom 2 (H)", 0xFFF1F5F9)

        // Covalent Bonds (Silver/Cyan rods)
        addCylinderBond(oPos, h1Pos, 0.12f, "covalent_bond_1", "Polar Covalent Bond 1", 0xFF94A3B8)
        addCylinderBond(oPos, h2Pos, 0.12f, "covalent_bond_2", "Polar Covalent Bond 2", 0xFF94A3B8)

        val dimensions = listOf(
            DimensionGuide("Bond Angle θ = 104.5°", h1Pos, h2Pos, Vector3(0f, -1f, 0f)),
            DimensionGuide("O-H Bond Length = 0.96 Å", oPos, h1Pos, Vector3(-1f, 0f, 0f))
        )

        return Mesh3D(
            vertices = vList,
            subMeshes = subMeshes,
            boundingRadius = 2.6f,
            dimensionGuides = dimensions
        )
    }

    // ==========================================
    // 7. OPTICAL PRISM (Physics - Light Refraction & Dispersion)
    // ==========================================
    fun createOpticalPrism(): Mesh3D {
        val vList = mutableListOf<Vector3>()
        val subMeshes = mutableListOf<SubMesh3D>()

        val s = 1.3f
        val h = 1.8f
        val halfH = h / 2f
        val sin60 = sqrt(3f) / 2f

        // Triangular prism vertices
        // Base triangle at -halfH
        val b0 = Vector3(0f, -halfH, s * sin60 * 0.66f)
        val b1 = Vector3(-s, -halfH, -s * sin60 * 0.33f)
        val b2 = Vector3(s, -halfH, -s * sin60 * 0.33f)

        // Top triangle at halfH
        val t0 = Vector3(0f, halfH, s * sin60 * 0.66f)
        val t1 = Vector3(-s, halfH, -s * sin60 * 0.33f)
        val t2 = Vector3(s, halfH, -s * sin60 * 0.33f)

        vList.addAll(listOf(b0, b1, b2, t0, t1, t2))

        val glassColor = 0x8838BDF8 // semi-transparent cyan glass

        val faces = listOf(
            // Bottom & Top triangles
            Face3D(0, 2, 1, "glass_prism", Vector3(0f, -1f, 0f), glassColor),
            Face3D(3, 4, 5, "glass_prism", Vector3(0f, 1f, 0f), glassColor),
            // Lateral Rectangles
            Face3D(0, 3, 4, "refracting_face_left", Vector3(-1f, 0f, 0.5f).normalize(), glassColor),
            Face3D(0, 4, 1, "refracting_face_left", Vector3(-1f, 0f, 0.5f).normalize(), glassColor),
            Face3D(0, 2, 5, "refracting_face_right", Vector3(1f, 0f, 0.5f).normalize(), glassColor),
            Face3D(0, 5, 3, "refracting_face_right", Vector3(1f, 0f, 0.5f).normalize(), glassColor),
            Face3D(1, 4, 5, "base_face", Vector3(0f, 0f, -1f), glassColor),
            Face3D(1, 5, 2, "base_face", Vector3(0f, 0f, -1f), glassColor)
        )

        val edges = listOf(
            Edge3D(0, 1), Edge3D(1, 2), Edge3D(2, 0),
            Edge3D(3, 4), Edge3D(4, 5), Edge3D(5, 3),
            Edge3D(0, 3), Edge3D(1, 4), Edge3D(2, 5)
        )

        subMeshes.add(SubMesh3D("glass_prism", "Dispersive Glass Prism", 0xFF0284C7, faces, edges))

        // Physics Light Dispersion Rays:
        // 1. Incident white beam entering from left
        val incidentBeam = LightRay(Vector3(-2.8f, 0f, 0.8f), Vector3(-0.6f, 0f, 0.25f), 0xFFFFFFFF, 6f, "White Incident Light")

        // 2. Dispersed rainbow rays exiting from right face
        val exitSource = Vector3(0.5f, 0f, 0.2f)
        val dispersionRays = listOf(
            incidentBeam,
            LightRay(exitSource, Vector3(2.5f, 0.45f, 0.9f), 0xFFEF4444, 4f, "Red (700 nm)"),
            LightRay(exitSource, Vector3(2.5f, 0.30f, 0.9f), 0xFFF97316, 4f, "Orange (620 nm)"),
            LightRay(exitSource, Vector3(2.5f, 0.15f, 0.9f), 0xFFFBBF24, 4f, "Yellow (580 nm)"),
            LightRay(exitSource, Vector3(2.5f, 0.00f, 0.9f), 0xFF10B981, 4f, "Green (530 nm)"),
            LightRay(exitSource, Vector3(2.5f, -0.15f, 0.9f), 0xFF06B6D4, 4f, "Cyan (490 nm)"),
            LightRay(exitSource, Vector3(2.5f, -0.30f, 0.9f), 0xFF3B82F6, 4f, "Blue (450 nm)"),
            LightRay(exitSource, Vector3(2.5f, -0.45f, 0.9f), 0xFF8B5CF6, 4f, "Violet (400 nm)")
        )

        val dimensions = listOf(
            DimensionGuide("Prism Apex Angle A = 60°", t0, t1, Vector3(0f, 1f, 0f)),
            DimensionGuide("Snell's Law: n₁ sin θ₁ = n₂ sin θ₂", Vector3(-2f, -1.2f, 0f), Vector3(2f, -1.2f, 0f), Vector3(0f, -1f, 0f))
        )

        return Mesh3D(
            vertices = vList,
            subMeshes = subMeshes,
            boundingRadius = 3.5f,
            dimensionGuides = dimensions,
            lightRays = dispersionRays
        )
    }
}
