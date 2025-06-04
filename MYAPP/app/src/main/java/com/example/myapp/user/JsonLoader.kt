package com.example.myapp.user


import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.myapp.R
import com.example.myapp.ui.screens.AttendanceStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import java.io.File
import android.util.Log


fun loadUsers(context: Context): List<User> {
    val json = context.assets.open("users.json").bufferedReader().use { it.readText() }
    val gson = Gson()
    val type = object : TypeToken<List<UserJson>>() {}.type
    val userJsonList: List<UserJson> = gson.fromJson(json, type)

    return userJsonList.map {
        User(
            email = it.email,
            password = it.password,
            role = Role.valueOf(it.role),
            name = it.name,
            photoResId = when (it.photoResId) {
                "teacher_photo" -> R.drawable.teacher_photo
                "student_photo" -> R.drawable.student_photo
                else -> R.drawable.default_avatar
            },
            studentInfo = if (it.role == "STUDENT") {
                StudentInfo(
                    groupeTD = it.groupeTD ?: "",
                    groupeTP = it.groupeTP ?: ""
                )
            } else null
        )
    }

}


data class UserJson(
    val email: String,
    val password: String,
    val role: String,
    val name: String,
    val photoResId: String,
    val groupeTD: String?,
    val groupeTP: String?
)

data class CoursJson(
    val nom: String,
    val type: String,
    val enseignantEmail: String,
    val date_cours: String
)

fun loadCourses(context: Context): List<Cours> {
    val json = context.assets.open("cours.json").bufferedReader().use { it.readText() }
    val gson = Gson()
    val type = object : TypeToken<List<CoursJson>>() {}.type
    val coursJsonList: List<CoursJson> = gson.fromJson(json, type)

    return coursJsonList.map {
        Cours(
            nom = it.nom.trim(),
            type = it.type.trim().uppercase(Locale.ROOT), // transforme en chaîne propre et uniforme
            enseignantEmail = it.enseignantEmail.trim(),
            date_cours = it.date_cours.trim()
        )
    }
}





fun loadPresences(context: Context): List<Presence> {
    ensurePresenceFileExists(context)  // Assure que le fichier existe
    val file = File(context.filesDir, "presences.json")
    val json = file.readText()
    val gson = Gson()
    val type = object : TypeToken<List<Presence>>() {}.type
    return gson.fromJson(json, type)
}

fun savePresences(context: Context, presences: List<Presence>) {
    val file = File(context.filesDir, "presences.json")
    val gson = Gson()
    val json = gson.toJson(presences)
    file.writeText(json)
    println("✅ Présences enregistrées dans ${file.absolutePath}")
}



fun ensurePresenceFileExists(context: Context) {
    val file = File(context.filesDir, "presences.json")
    if (!file.exists()) {
        val input = context.assets.open("presences.json")
        val text = input.bufferedReader().use { it.readText() }
        file.writeText(text)
    }
}



