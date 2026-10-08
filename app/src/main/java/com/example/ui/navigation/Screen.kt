package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data class SubjectDetail(val subjectId: String) : Screen("subject/$subjectId") {
        companion object {
            const val ROUTE = "subject/{subjectId}"
            fun createRoute(subjectId: String) = "subject/$subjectId"
        }
    }
    data class Viewer(val modelId: String) : Screen("viewer/$modelId") {
        companion object {
            const val ROUTE = "viewer/{modelId}"
            fun createRoute(modelId: String) = "viewer/$modelId"
        }
    }
}
