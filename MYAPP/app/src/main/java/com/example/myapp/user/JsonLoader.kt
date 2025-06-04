package com.example.myapp.user


import android.content.Context
import com.example.myapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
            }
        )
    }
}

data class UserJson(
    val email: String,
    val password: String,
    val role: String,
    val name: String,
    val photoResId: String
)
