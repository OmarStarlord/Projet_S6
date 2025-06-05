package com.example.myapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AttendanceStatus {
    NONE, PRESENT, LATE, ABSENT
}

// ce code est pour la page où le prof sélectionne si l'etudiant present, absent, ou en retard
@Composable
fun PresenceScreen(session: String) {
    val studentList = listOf(
        "Louay Ben Ltoufa",
        "Baratte Martin",
        "Ragavan Sakithyan",
        "Beauvy Elise",
        "Omar Zahraman",
        "Omar Lidalt"
    )
    // la liste sera utlisé pour les toutes les sessions de 8h à 18h
    val attendanceMap = remember {
        mutableStateMapOf<String, AttendanceStatus>().apply {
            studentList.forEach { this[it] = AttendanceStatus.NONE }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Présences – Séance $session",
            fontSize = 22.sp,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem("Présent", Color(0xFF66BB6A))    //legende pour comprendre code couleur
            LegendItem("En retard", Color(0xFFFFB74D))
            LegendItem("Absent", Color(0xFFff0000))
        }

        Spacer(modifier = Modifier.height(8.dp))

        studentList.forEach { studentName ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7E57C2)) // violet
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = studentName,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    //changement de couleur bouton
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatusCircle(
                            baseColor = Color(0xFF143e01),
                            highlightColor = Color(0xFF66BB6A),
                            selected = attendanceMap[studentName] == AttendanceStatus.PRESENT
                        ) {
                            attendanceMap[studentName] = AttendanceStatus.PRESENT
                        }
                        StatusCircle(
                            baseColor = Color(0xFF6a4804),
                            highlightColor = Color(0xFFFFB74D),
                            selected = attendanceMap[studentName] == AttendanceStatus.LATE
                        ) {
                            attendanceMap[studentName] = AttendanceStatus.LATE
                        }
                        StatusCircle(
                            baseColor = Color(0xFF710404),
                            highlightColor = Color(0xFFff0000),
                            selected = attendanceMap[studentName] == AttendanceStatus.ABSENT
                        ) {
                            attendanceMap[studentName] = AttendanceStatus.ABSENT
                        }
                    }
                }
            }
        }
    }
}
// les cercles
@Composable
fun StatusCircle(
    baseColor: Color,
    highlightColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val displayColor = if (selected) highlightColor else baseColor
    val circleSize = if (selected) 24.dp else 20.dp
    val borderWidth = if (selected) 3.dp else 1.dp
    val borderColor = if (selected) Color.White else Color.Gray
    // ajouter une bordure au bouton quand il est selectionné
    Box(
        modifier = Modifier
            .size(circleSize)
            .background(color = displayColor, shape = CircleShape)
            .clickable { onClick() }
            .border(width = borderWidth, color = borderColor, shape = CircleShape)
    )
}
// pour afficher la legende en haut
@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = Color.White, fontSize = 12.sp)
    }
}
