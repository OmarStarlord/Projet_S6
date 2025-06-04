package com.example.myapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.user.*

enum class AttendanceStatus {
    NONE, PRESENT, LATE, ABSENT
}

@Composable
fun PresenceScreen(session: String, cours: Cours) {
    val context = LocalContext.current
    val allUsers = remember { loadUsers(context) }
    val students = allUsers.filter { it.role == Role.STUDENT }

    val displayedStudents = when (cours.type.trim().uppercase()) {
        "CM" -> students
        "TD1", "TD2", "TD3" -> students.filter { it.studentInfo?.groupeTD == cours.type }
        "TP1", "TP2", "TP3" -> students.filter { it.studentInfo?.groupeTP == cours.type }
        else -> emptyList()
    }

    val attendanceMap = remember(displayedStudents) {
        mutableStateMapOf<String, AttendanceStatus>().apply {
            displayedStudents.forEach { this[it.email] = AttendanceStatus.NONE }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Présences – Séance $session",
            fontSize = 22.sp,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem("Présent", Color(0xFF143e01))
            LegendItem("En retard", Color(0xFF6a4804))
            LegendItem("Absent", Color(0xFF710404))
        }

        Spacer(modifier = Modifier.height(8.dp))

        displayedStudents.forEach { student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = student.name,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusCircle(Color(0xFF143e01), Color(0xFF66BB6A), attendanceMap[student.email] == AttendanceStatus.PRESENT) {
                        attendanceMap[student.email] = AttendanceStatus.PRESENT
                    }
                    StatusCircle(Color(0xFF6a4804), Color(0xFFFFB74D), attendanceMap[student.email] == AttendanceStatus.LATE) {
                        attendanceMap[student.email] = AttendanceStatus.LATE
                    }
                    StatusCircle(Color(0xFF710404), Color(0xFFFF0000), attendanceMap[student.email] == AttendanceStatus.ABSENT) {
                        attendanceMap[student.email] = AttendanceStatus.ABSENT
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val etudiants = displayedStudents.map {
                    EtudiantPresence(
                        email = it.email,
                        statut = when (attendanceMap[it.email]) {
                            AttendanceStatus.PRESENT -> StatutPresence.PRESENT
                            AttendanceStatus.LATE -> StatutPresence.JUSTIFIE
                            AttendanceStatus.ABSENT -> StatutPresence.ABSENT
                            else -> StatutPresence.ABSENT
                        }
                    )
                }

                val presence = Presence(
                    coursId = cours.nom,
                    date = cours.date_cours.take(10),
                    typeCours = cours.type,
                    etudiants = etudiants
                )

                val existing = loadPresences(context).toMutableList()
                existing.removeIf { it.coursId == presence.coursId && it.date == presence.date }
                existing.add(presence)

                savePresences(context, existing)
                Log.d("PresenceScreen", "✅ Présence sauvegardée pour ${presence.coursId} - ${presence.date}")
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Enregistrer")
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
        Text(text = label, color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
    }
}
