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
                password = "gaussestmeilleurquenewton",
                role = Role.TEACHER,
                name = "M. Joel Dion",
                photoResId = com.example.myapp.R.drawable.teacher_photo
            ),
            User(
                email = "louay.ben-ltoufa@uha.fr",
                password = "zanak123",
                role = Role.STUDENT,
                name = "Ben Ltoufa Louay",
                photoResId = com.example.myapp.R.drawable.student_photo
            ),
            User(
                email = "martin.baratte@uha.fr",
                password = "zanak123",
                role = Role.STUDENT,
                name = "Baratte Martin",
                photoResId = com.example.myapp.R.drawable.student_photo
            ),
            User(
                email = "sakithyan.ragavan@uha.fr",
                password = "zanak123",
                role = Role.STUDENT,
                name = "Ragavan Sakithyan",
                photoResId = com.example.myapp.R.drawable.student_photo
            ),
            User(
                email = "elise.beauvy@uha.fr",
                password = "zanak123",
                role = Role.STUDENT,
                name = "Beauvy Elise",
                photoResId = com.example.myapp.R.drawable.student_photo
            ),
            User(
                email = "omar.zahraman@uha.fr",
                password = "zanak123",
                role = Role.STUDENT,
                name = "Omar Zahraman",
                photoResId = com.example.myapp.R.drawable.student_photo
            ),
            User(
                email = "omar.lidalt@uha.fr",
                password = "zanak123",
                role = Role.STUDENT,
                name = "Omar Lidalt",
                photoResId = com.example.myapp.R.drawable.student_photo
            )

        )
    }
}
