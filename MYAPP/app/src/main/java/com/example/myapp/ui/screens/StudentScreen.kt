package com.example.myapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.user.Presence
import com.example.myapp.user.StatutPresence
import com.example.myapp.user.loadPresences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun StudentScreen(studentEmail: String) {
    Log.d("DEBUG_STUDENT", "StudentScreen loaded with email: $studentEmail")
    val context = LocalContext.current

    // 1) Launch loadPresences(...) on Dispatchers.IO and log filenames
    val presencesState = produceState<List<Presence>?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            val list = loadPresences(context)
            Log.d("DEBUG_PRESENCES", "Loaded ${list.size} presence(s):")
            list.forEach { presence ->
                Log.d("DEBUG_PRESENCES", "→ ${presence.coursId} @ ${presence.date} (${presence.etudiants.size} students)")
            }
            list
        }
    }

    // 2) While loading, show a spinner
    if (presencesState.value == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 3) Once loaded, unwrap the list
    val presences = presencesState.value!!
    Log.d("DEBUG_STUDENT", "Found ${presences.size} presence files (state)")

    // 4) Compute stats for this student
    val totalPresent = presences.sumOf { presence ->
        presence.etudiants.count {
            it.email.trim().equals(studentEmail.trim(), ignoreCase = true)
                    && it.statut == StatutPresence.PRESENT
        }
    }
    val totalAbsent = presences.sumOf { presence ->
        presence.etudiants.count {
            it.email.trim().equals(studentEmail.trim(), ignoreCase = true)
                    && it.statut == StatutPresence.ABSENT
        }
    }
    val totalJustifie = presences.sumOf { presence ->
        val count = presence.etudiants.count {
            it.email.trim().equals(studentEmail.trim(), ignoreCase = true)
                    && it.statut == StatutPresence.JUSTIFIE
        }
        if (count > 0) {
            Log.d(
                "DEBUG_STATUT",
                "🟡 $studentEmail has JUSTIFIE in ${presence.coursId} on ${presence.date}"
            )
        }
        count
    }

    // 5) Display stats
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
fun StatBox(label: String, count: Int, color: Color) {
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
