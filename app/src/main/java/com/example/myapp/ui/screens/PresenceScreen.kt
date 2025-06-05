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
import com.example.myapp.user.StudentGroups

enum class AttendanceStatus {
    NONE, PRESENT, LATE, ABSENT
}

@Composable
fun PresenceScreen(session: String) {
    val studentList = StudentGroups.groups[session] ?: emptyList()

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
        //legende absent rouge, present vert et retard orange
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem("Présent", Color(0xFF66BB6A))
            LegendItem("En retard", Color(0xFFFFB74D))
            LegendItem("Absent", Color(0xFFff0000))
        }

        Spacer(modifier = Modifier.height(8.dp))
        //liste des etudiants avec butons à selectionner
        studentList.forEach { studentName ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7E57C2))
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
                //changement de couleur en fonction du statut
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

    Box(
        modifier = Modifier
            .size(circleSize)
            .background(color = displayColor, shape = CircleShape)
            .clickable { onClick() }
            .border(width = borderWidth, color = borderColor, shape = CircleShape)
    )
}

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
