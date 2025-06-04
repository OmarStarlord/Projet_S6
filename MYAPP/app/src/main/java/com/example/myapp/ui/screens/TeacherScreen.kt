package com.example.myapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.R
import com.example.myapp.user.User

// c'est l'interface de l'espace prof

@Composable
fun TeacherScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val teacher = User.users.find { it.role == com.example.myapp.user.Role.TEACHER }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.teacher_photo),
                contentDescription = "photo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Bienvenue dans l’espace Cloky enseignant !",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onBackground
                )
                Text(
                    text = teacher?.name ?: "Nom inconnu",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onBackground
                )
            }
        }

       // liste pour les seances en fonction de l'heure
        val sessions = listOf(
            "8h-10h",
            "10h-12h",
            "14h-16h",
            "16h-18h"
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sessions.size) { index ->
                val session = sessions[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .clickable {
                            navController.navigate("presence/$session")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Séance $session",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}
