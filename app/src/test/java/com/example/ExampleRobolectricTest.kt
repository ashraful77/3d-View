package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ModelRepository
import com.example.domain.SubjectId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("3D Learning Lab", appName)
  }

  @Test
  fun `repository loads all subjects and prototype models`() {
    val repo = ModelRepository.instance
    val subjects = repo.getSubjects()
    assertTrue(subjects.isNotEmpty())
    assertEquals(5, subjects.size)

    val mathModels = repo.getModelsForSubject(SubjectId.MATHEMATICS)
    assertTrue(mathModels.any { it.id == "cube" })
    assertTrue(mathModels.any { it.id == "cylinder" })
    assertTrue(mathModels.any { it.id == "cone" })
    assertTrue(mathModels.any { it.id == "sphere" })

    val bioModels = repo.getModelsForSubject(SubjectId.BIOLOGY)
    assertTrue(bioModels.any { it.id == "heart" })
  }
}
