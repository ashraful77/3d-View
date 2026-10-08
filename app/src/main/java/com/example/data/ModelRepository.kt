package com.example.data

import com.example.domain.ModelObject3D
import com.example.domain.Subject
import com.example.domain.SubjectId
import com.example.viewer3d.model.Mesh3D
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ModelRepository {

    private val _bookmarkedIds = MutableStateFlow<Set<String>>(setOf("heart", "cube", "cylinder"))
    val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds.asStateFlow()

    fun getSubjects(): List<Subject> = BuiltInModels.SUBJECTS

    fun getSubject(id: SubjectId): Subject? =
        BuiltInModels.SUBJECTS.find { it.id == id }

    fun getAllModels(): List<ModelObject3D> = BuiltInModels.ALL_MODELS

    fun getModel(id: String): ModelObject3D? =
        BuiltInModels.ALL_MODELS.find { it.id == id }

    fun getModelsForSubject(subjectId: SubjectId): List<ModelObject3D> =
        BuiltInModels.ALL_MODELS.filter { it.subjectId == subjectId }

    fun getModelsForCategory(categoryId: String): List<ModelObject3D> =
        BuiltInModels.ALL_MODELS.filter { it.categoryId == categoryId }

    fun searchModels(query: String): List<ModelObject3D> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return BuiltInModels.ALL_MODELS
        return BuiltInModels.ALL_MODELS.filter { model ->
            model.name.lowercase().contains(q) ||
            model.description.lowercase().contains(q) ||
            model.tags.any { it.lowercase().contains(q) } ||
            model.parts.any { it.name.lowercase().contains(q) }
        }
    }

    fun toggleBookmark(modelId: String) {
        val current = _bookmarkedIds.value
        _bookmarkedIds.value = if (current.contains(modelId)) {
            current - modelId
        } else {
            current + modelId
        }
    }

    fun isBookmarked(modelId: String): Boolean =
        _bookmarkedIds.value.contains(modelId)

    fun getMeshForModel(modelId: String): Mesh3D =
        BuiltInModels.getMeshForModel(modelId)

    companion object {
        val instance by lazy { ModelRepository() }
    }
}
