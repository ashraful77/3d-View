package com.example

import androidx.compose.ui.geometry.Offset
import com.example.data.BuiltInModels
import com.example.viewer3d.engine.Camera3D
import com.example.viewer3d.engine.Renderer3D
import com.example.viewer3d.engine.ShadingMode
import com.example.viewer3d.engine.ViewerState
import com.example.viewer3d.math.Matrix4
import com.example.viewer3d.math.Vector3
import com.example.viewer3d.model.ShapeGenerators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testVector3Operations() {
    val v1 = Vector3(1f, 2f, 3f)
    val v2 = Vector3(4f, 5f, 6f)

    val sum = v1 + v2
    assertEquals(5f, sum.x, 0.001f)
    assertEquals(7f, sum.y, 0.001f)
    assertEquals(9f, sum.z, 0.001f)

    val dot = v1.dot(v2)
    assertEquals(32f, dot, 0.001f)

    val cross = Vector3(1f, 0f, 0f).cross(Vector3(0f, 1f, 0f))
    assertEquals(0f, cross.x, 0.001f)
    assertEquals(0f, cross.y, 0.001f)
    assertEquals(1f, cross.z, 0.001f)
  }

  @Test
  fun testMatrix4Transform() {
    val mTrans = Matrix4.translation(2f, 3f, 4f)
    val p = Vector3(1f, 1f, 1f)
    val transformed = mTrans.transformPoint(p)

    assertEquals(3f, transformed.x, 0.001f)
    assertEquals(4f, transformed.y, 0.001f)
    assertEquals(5f, transformed.z, 0.001f)
  }

  @Test
  fun testCubeGenerationAndUnfolding() {
    val cube = ShapeGenerators.createCube()
    assertTrue(cube.vertices.isNotEmpty())
    assertTrue(cube.subMeshes.size >= 6)
    assertNotNull(cube.netMorpher)

    // Verify net unfold morpher runs cleanly without crash
    val unfolded = cube.netMorpher?.invoke(1.0f)
    assertNotNull(unfolded)
    assertTrue(unfolded!!.vertices.isNotEmpty())
  }

  @Test
  fun testHeartGenerationAndCardiacPulse() {
    val heart = ShapeGenerators.createHumanHeart()
    assertTrue(heart.vertices.isNotEmpty())
    assertTrue(heart.subMeshes.any { it.partId == "left_ventricle" })
    assertTrue(heart.subMeshes.any { it.partId == "aorta" })
    assertNotNull(heart.animationMorpher)

    // Verify cardiac pulse morpher runs cleanly
    val pulsed = heart.animationMorpher?.invoke(0.2f)
    assertNotNull(pulsed)
  }

  @Test
  fun testCubeRendererExecution() {
    val cubeMesh = ShapeGenerators.createCube()
    val cubeModel = BuiltInModels.CUBE
    val state = ViewerState(
      camera = Camera3D.DEFAULT,
      shadingMode = ShadingMode.SOLID_SHADED,
      showLabels = true,
      showDimensions = true
    )

    val scene = Renderer3D.render(
      mesh = cubeMesh,
      camera = state.camera,
      viewportWidth = 1080f,
      viewportHeight = 1920f,
      state = state,
      labelsData = cubeModel.labels
    )

    assertNotNull(scene)
    assertTrue(scene.faces.isNotEmpty())

    // Test hit testing
    val hit = Renderer3D.hitTestPart(Offset(540f, 960f), scene)
    // Should not crash
  }
}
