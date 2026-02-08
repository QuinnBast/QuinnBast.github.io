package com.calian.at.demoAuth.solutions.lesson7_ldap

import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.Base64
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


interface UserDatabaseInterface {
    fun getUser(username: String): User?
    fun addUser(username: String, password: String, roles: List<Role>)
    fun verifyUser(username: String, password: String): Boolean
}

class UserDatabase : UserDatabaseInterface {

    // Pretend we are a database.
    val users = mutableMapOf<String, User>()

    init {
        addUser("Quinn", "Bast", listOf(Role.ADMIN, Role.USER))
        addUser("Alice", "Wonderland", listOf(Role.VIEWER))
        addUser("Bobby", "Tables", listOf(Role.ADMIN))
    }

    override fun getUser(username: String): User? {
        return users[username]
    }

    override fun addUser(username: String, password: String, roles: List<Role>) {
        val random: SecureRandom = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)

        users[username] = User(
            username,
            Base64.getEncoder().encodeToString(salt),
            hashString(password, salt),
            roles
        )
    }

    override fun verifyUser(username: String, password: String): Boolean {
        val user = users[username]
        if(user != null) {
            return hashString(password, Base64.getDecoder().decode(user.salt)) == user.hashedPassword
        }
        return false
    }

    private fun hashString(value: String, salt: ByteArray): String {
        val spec: KeySpec = PBEKeySpec(value.toCharArray(), salt, 65536, 128)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash: ByteArray = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(hash)
    }
}

fun main() {
    val db = UserDatabase()
    for (user in db.users) {
        val user = user.value
        val decodedSalt = user.salt
        println(user.username + "," + decodedSalt + "," + user.hashedPassword)

        val userSession = UserSession(
            user,
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            System.currentTimeMillis() + 1000 * 60 * 60
        )

        val base64 = Base64.getEncoder().encodeToString(Json.encodeToString(userSession).toByteArray())
        val token = Json.encodeToString(base64)
        println("Token: $token")
    }
}