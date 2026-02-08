package com.calian.at.demoAuth.solutions.lesson3_rbac

interface UserDatabaseInterface {
    fun getUser(username: String): User?
    fun addUser(user: User)
    fun verifyUser(username: String, password: String): Boolean
}

class UserDatabase : UserDatabaseInterface {

    // Pretend we are a database.
    private val users = mutableListOf<User>(
        User("Quinn", "Bast", listOf(Role.ADMIN, Role.USER)),
        User("Alice", "Wonderland", listOf(Role.VIEWER)),
        User("Bobby", "Tables", listOf(Role.ADMIN)),
    )

    override fun getUser(username: String): User? {
        return users.find { it.username == username }
    }

    override fun addUser(user: User) {
        users.add(user)
    }

    override fun verifyUser(username: String, password: String): Boolean {
        return users.any { it.username == username && it.password == password }
    }
}