package com.example.domain

import androidx.compose.ui.graphics.Color

enum class SubjectId(val rawId: String) {
    MATHEMATICS("mathematics"),
    BIOLOGY("biology"),
    PHYSICS("physics"),
    CHEMISTRY("chemistry"),
    OTHERS("others");

    companion object {
        fun fromString(id: String): SubjectId =
            entries.find { it.rawId.equals(id, ignoreCase = true) } ?: MATHEMATICS
    }
}

enum class Capability {
    ROTATE,
    ZOOM,
    PAN,
    LABELS,
    SELECTION,
    DIMENSIONS,
    NET_UNFOLD,
    ANIMATION,
    CUTAWAY_XRAY
}

data class Subject(
    val id: SubjectId,
    val name: String,
    val description: String,
    val accentColor: Color,
    val iconName: String,
    val categories: List<Category>
)

data class Category(
    val id: String,
    val subjectId: SubjectId,
    val name: String,
    val description: String
)

data class ModelPart(
    val id: String,
    val name: String,
    val description: String,
    val pedagogicalNotes: String,
    val colorHex: Long,
    val formulaOrDetail: String = ""
)

data class ModelLabel(
    val id: String,
    val text: String,
    val targetPartId: String,
    val anchorX: Float,
    val anchorY: Float,
    val anchorZ: Float,
    val visibleByDefault: Boolean = true
)

data class ModelDimensions(
    val title: String,
    val formulas: List<Pair<String, String>>,
    val sampleMeasurements: List<Pair<String, String>>
)

data class AnimationMetadata(
    val hasAnimation: Boolean,
    val name: String = "",
    val description: String = "",
    val canPlay: Boolean = false,
    val defaultBpm: Int = 72,
    val isLooping: Boolean = true
)

data class EducationalInfo(
    val definition: String,
    val keyPoints: List<String>,
    val realWorldApplications: List<String>,
    val teacherTips: List<String>,
    val discussionQuestions: List<String> = emptyList()
)

data class ModelObject3D(
    val id: String,
    val name: String,
    val subjectId: SubjectId,
    val categoryId: String,
    val description: String,
    val capabilities: Set<Capability>,
    val parts: List<ModelPart>,
    val labels: List<ModelLabel>,
    val dimensions: ModelDimensions?,
    val animationInfo: AnimationMetadata?,
    val educationalInfo: EducationalInfo,
    val classLevel: String = "Grades 6–12",
    val tags: List<String> = emptyList()
)
