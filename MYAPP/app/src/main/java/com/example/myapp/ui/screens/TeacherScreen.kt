package com.example.myapp.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.R
import com.example.myapp.user.Cours
import com.example.myapp.user.loadCourses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TeacherScreen(navController: NavController, teacherEmail: String) {
    val context = LocalContext.current

    // 1) Launch loadCourses(...) on Dispatchers.IO
    val coursesState = produceState<List<Cours>?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            loadCourses(context)
        }
    }

    // 2) While loading from Pi, show a spinner
    if (coursesState.value == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 3) Once loaded, unwrap the list
    val coursList = coursesState.value!!

    // Compute today’s date string
    val todayString = remember {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        format.format(Date())
    }

    // Filter courses for this teacher and today
    val todayCourses = coursList.filter {
        it.enseignantEmail.trim() == teacherEmail.trim() &&
                it.date_cours.trim().take(10) == todayString
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = teacherEmail,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(todayCourses.size) { index ->
                val cours = todayCourses[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("selectedCours", cours)
                            navController.navigate("presence")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = cours.nom, style = MaterialTheme.typography.titleLarge)
                        Text(text = cours.type, style = MaterialTheme.typography.bodyMedium)
                        Text(text = cours.date_cours, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
