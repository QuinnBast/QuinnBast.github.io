package com.calian.at.demoAuth.solutions.lesson2_sessions

interface UserDatabaseInterface {
    fun getUser(username: String): User?
    fun addUser(user: User)
    fun verifyUser(username: String, password: String): Boolean
}

class UserDatabase : UserDatabaseInterface {

    // Pretend we are a database.
    private val users = mutableListOf<User>(
        User("Quinn", "Bast"),
        User("Alice", "Wonderland"),
        User("Bobby", "Tables"),
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