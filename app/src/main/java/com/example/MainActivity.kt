package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.ModelRepository
import com.example.ui.navigation.Screen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ModelViewerScreen
import com.example.ui.screens.SubjectDetailScreen
import com.example.ui.theme.LearningLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LearningLabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF090D16)
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val repository = remember { ModelRepository.instance }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                repository = repository,
                onNavigateToSubject = { subjectId ->
                    navController.navigate(Screen.SubjectDetail.createRoute(subjectId))
                },
                onNavigateToViewer = { modelId ->
                    navController.navigate(Screen.Viewer.createRoute(modelId))
                }
            )
        }

        // Subject Detail Screen
        composable(
            route = Screen.SubjectDetail.ROUTE,
            arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: "mathematics"
            SubjectDetailScreen(
                subjectIdString = subjectId,
                repository = repository,
                onBack = { navController.popBackStack() },
                onNavigateToViewer = { modelId ->
                    navController.navigate(Screen.Viewer.createRoute(modelId))
                }
            )
        }

        // 3D Model Viewer Screen
        composable(
            route = Screen.Viewer.ROUTE,
            arguments = listOf(navArgument("modelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString("modelId") ?: "cube"
            ModelViewerScreen(
                modelId = modelId,
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
