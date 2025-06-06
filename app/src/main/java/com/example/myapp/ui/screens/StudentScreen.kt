package com.example.myapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapp.R

@Composable
fun StudentScreen(navController: NavController) {
    val studentName = "Louay Ben Ltoufa"
    val totalAbsences = remember { mutableIntStateOf(3) }

    val sessionList = listOf("TD1", "TD2", "TP1", "TP2", "CM")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1er bloc photo + phrase bienvenu...
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.student_photo),
                contentDescription = "Photo étudiant",
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Gray, shape = CircleShape),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = "Bienvenue dans Cloky Student",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = studentName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        // Compteur d absences
        Text(
            text = "Nombre total d'absences : ${totalAbsences.value}",
            fontSize = 18.sp,
            color = Color(0xFFFFB74D),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Liste des séances
        sessionList.forEach { session ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("studentSessionDetails/$session")
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF5E35B1))
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Séance $session",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "Détails",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
