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
    val photoResId: Int
) {
    companion object {
        val users = listOf(
            User(
                email = "joel.dion@uha.fr",
                password = "dionlegoat",
                role = Role.TEACHER,
                name = "M. Joel Dion",
                photoResId = com.example.myapp.R.drawable.teacher_photo
            ),
            User(
                email = "louay.ben-ltoufa@uha.fr",
                password = "zanak123",
                role = Role.STUDENT,
                name = "Louay Ben Ltoufa",
                photoResId = com.example.myapp.R.drawable.student_photo
            )
        )
    }
}
