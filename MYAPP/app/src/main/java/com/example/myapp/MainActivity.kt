package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myapp.ui.screens.*
import com.example.myapp.ui.theme.MYAPPTheme
import com.example.myapp.user.Cours
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MYAPPTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {

                    // Login screen
                    composable("login") {
                        LoginScreen(navController = navController)
                    }


                    composable(
                        route = "teacher/{email}",
                        arguments = listOf(navArgument("email") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        TeacherScreen(navController = navController, teacherEmail = email)
                    }


                    composable(
                        route = "student/{email}",
                        arguments = listOf(navArgument("email") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        StudentScreen(studentEmail = email)
                    }

                    // Presence tracking screen (can be improved later to take more params)
                    composable("presence") {
                        val cours = navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.get<Cours>("selectedCours")

                        if (cours != null) {
                            PresenceScreen(session = cours.nom, cours = cours)
                        } else {
                            // Affiche une erreur ou redirige
                            Text("Erreur : cours non trouvé")
                        }
                    }


                }
            }
        }
    }
}
