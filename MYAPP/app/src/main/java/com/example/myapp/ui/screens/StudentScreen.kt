package com.example.myapp.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.user.Presence
import com.example.myapp.user.StatutPresence
import com.example.myapp.user.loadPresences

@Composable
fun StudentScreen(studentEmail: String) {
    val context = LocalContext.current
    val presences = remember { loadPresences(context) }

    val totalPresent = presences.sumOf { presence ->
        presence.etudiants.count { it.email == studentEmail && it.statut == StatutPresence.PRESENT }
    }
    val totalAbsent = presences.sumOf { presence ->
        presence.etudiants.count { it.email == studentEmail && it.statut == StatutPresence.ABSENT }
    }
    val totalJustifie = presences.sumOf { presence ->
        presence.etudiants.count { it.email == studentEmail && it.statut == StatutPresence.JUSTIFIE }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenue 👋",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Voici ton bilan de présence",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StatBox("Présent", totalPresent, MaterialTheme.colorScheme.primary)
                    StatBox("Absent", totalAbsent, MaterialTheme.colorScheme.tertiary)
                    StatBox("Justifié", totalJustifie, MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
