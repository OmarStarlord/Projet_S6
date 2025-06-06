package com.example.myapp.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.myapp.R
import com.example.myapp.user.Role
import com.example.myapp.user.User
import com.example.myapp.user.loadUsers

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current

    // 1) Launch loadUsers(...) on IO dispatcher
    val usersState = produceState<List<User>?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            loadUsers(context)
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // 2) While loading from Pi (usersState.value == null), show a spinner
    if (usersState.value == null) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 3) Once loaded, unwrap the list of users
    val users = usersState.value!!

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoomar),
                contentDescription = "Logo de l'application",
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "Connexion",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
            )

            Button(
                onClick = {
                    val matchedUser = users.find { it.email == email && it.password == password }
                    if (matchedUser != null) {
                        when (matchedUser.role) {
                            Role.TEACHER -> navController.navigate("teacher/${matchedUser.email}")
                            Role.STUDENT -> navController.navigate("student/${matchedUser.email}")
                        }
                    } else {
                        error = "Email ou mot de passe incorrect"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Se connecter")
            }

            if (error != null) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }
    }
}
