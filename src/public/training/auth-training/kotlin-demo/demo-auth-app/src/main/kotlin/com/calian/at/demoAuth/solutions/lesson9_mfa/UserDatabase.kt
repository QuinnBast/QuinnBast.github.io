package com.calian.at.demoAuth.solutions.lesson9_mfa

import com.bastiaanjansen.otp.HMACAlgorithm
import com.bastiaanjansen.otp.HOTPGenerator
import com.bastiaanjansen.otp.TOTPGenerator
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.time.Duration
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


interface UserDatabaseInterface {
    fun getUser(username: String): User?
    fun addUser(username: String, password: String, roles: List<Role>)
    fun verifyUser(username: String, password: String, otpGuess: String): Boolean
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
            username, // To make things easy, I'm just going to re-use the users salt as their OTP secret.
            roles
        )
    }

    override fun verifyUser(username: String, password: String, otpGuess: String): Boolean {
        val user = users[username]
        if(user != null) {
            if(hashString(password, Base64.getDecoder().decode(user.salt)) == user.hashedPassword) {
                // If the password is correct, we can check the OTP
                val totpGenerator = TotpGenerator(user.optSecret)
                if(totpGenerator.verifyTOTP(otpGuess)) {
                    return true
                }
            }
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
    TotpGenerate()
    HotpGenerate()
    dumpUserOtpUrls()
}

fun TotpGenerate() {
    val secret = "ILoveCalian".toByteArray()
    val totpGenerator = TOTPGenerator.Builder(secret)
        .withHOTPGenerator { builder: HOTPGenerator.Builder ->
            builder.withPasswordLength(6)
            builder.withAlgorithm(HMACAlgorithm.SHA256) // SHA256 and SHA512 are also supported
        }
        .withPeriod(Duration.ofSeconds(30))
        .build()

    // Generate a URL that defines our HOTP generator and the current counter value (we will use 0):
    val generatorUrl = totpGenerator.getURI("QuinnBastCalian")
    println("TOTP URL: $generatorUrl")

    println("Current TOTP is: ${totpGenerator.now()}")
}

fun HotpGenerate() {
    // Create some random secret key
    val secret = "ILoveCalian".toByteArray()

    // Initialize the HOTP generator with an 8 digit code and our secret
    val hotpGenerator: HOTPGenerator = HOTPGenerator.Builder(secret)
        .withPasswordLength(8)
        .withAlgorithm(HMACAlgorithm.SHA256)
        .build()

    // Generate a URL that defines our HOTP generator and the current counter value (we will use 0):
    val generatorUrl = hotpGenerator.getURI(0,"QuinnBastCalian")
    println("HOTP URL: $generatorUrl")

    // Emulate a counter that iterates the user's code generations
    for(counter in 0..9) {
        val code = hotpGenerator.generate(counter.toLong())
        println("HOTP code for attempt $counter: $code")
    }
}

fun dumpUserOtpUrls() {
    val userDatabase = UserDatabase()
    for(user in userDatabase.users.values) {
        val totpGenerator = TotpGenerator(user.optSecret)
        println("User: ${user.username} OTP URL: ${totpGenerator.getUrl(user.username)}")
    }
}