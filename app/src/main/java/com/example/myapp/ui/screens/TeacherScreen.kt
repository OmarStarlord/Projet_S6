package com.example.myapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapp.R
import com.example.myapp.user.User

@OptIn(ExperimentalMaterial3Api::class)
// c'est l'interface de l'espace prof
@Composable
fun TeacherScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val teacher = User.users.find { it.role == com.example.myapp.user.Role.TEACHER }
    val openDatePicker = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(16.dp)
    ) {
        // espace prof, photo + bienvenue....
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
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

        Spacer(modifier = Modifier.height(16.dp))

        // on peut afficher la date, mais instead j'ai mis le mot "aujourdhui" avec le calendrier
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Aujourd'hui",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { openDatePicker.value = true }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Ouvrir le calendrier",
                    tint = colorScheme.onBackground
                )
            }
        }

        // la listes des seances, chacune aura un tag specifique td1/cm/tp....
        val sessions = listOf("TD1", "TD2", "TP1", "CM")
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sessions.size) { index ->
                val session = sessions[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            navController.navigate("presence/$session")
                        },
                    colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
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

        // l'affichage du calendrier
        if (openDatePicker.value) {
            DatePickerDialog(
                onDismissRequest = { openDatePicker.value = false },
                confirmButton = {
                    TextButton(onClick = { openDatePicker.value = false }) {
                        Text("Fermer")
                    }
                }
            ) {
                DatePicker(
                    state = rememberDatePickerState(),
                    showModeToggle = true
                )
            }
        }
    }
}
