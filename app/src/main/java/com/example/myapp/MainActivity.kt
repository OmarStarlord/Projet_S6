package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myapp.ui.screens.*
import com.example.myapp.ui.theme.MYAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MYAPPTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(navController = navController)
                    }
                    composable("teacher") {
                        TeacherScreen(navController = navController)
                    }
                    composable("student") {
                        StudentScreen(navController = navController)
                    }
                    composable(
                        route = "presence/{session}",
                        arguments = listOf(navArgument("session") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        val session = backStackEntry.arguments?.getString("session") ?: ""
                        PresenceScreen(session = session)
                    }

                    composable(
                        route = "studentSessionDetails/{session}",
                        arguments = listOf(navArgument("session") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        val session = backStackEntry.arguments?.getString("session") ?: ""
                        DetailsScreen(session = session)
                    }
                }
            }
        }
    }
}
