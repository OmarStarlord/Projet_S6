package com.example.myapp.user

enum class Role {
    TEACHER,
    STUDENT
}

data class User(
    val email: String,
    val password: String,
    val role: Role,
    val name: String,
    val photoResId: Int,
    val studentInfo: StudentInfo? = null
)

data class StudentInfo(
    val groupeTD: String,
    val groupeTP: String
)
