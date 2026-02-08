package com.calian.at.demoAuth.solutions.lesson9_mfa

import com.bastiaanjansen.otp.HMACAlgorithm
import com.bastiaanjansen.otp.HOTPGenerator
import com.bastiaanjansen.otp.TOTPGenerator
import java.time.Duration

class TotpGenerator(secret: String) {

    val generator: TOTPGenerator = TOTPGenerator.Builder(secret.toByteArray())
        .withHOTPGenerator { builder: HOTPGenerator.Builder ->
            builder.withPasswordLength(6)
            builder.withAlgorithm(HMACAlgorithm.SHA256) // SHA256 and SHA512 are also supported
        }
        .withPeriod(Duration.ofSeconds(30))
        .build()

    fun verifyTOTP(guess: String): Boolean {
        println("Current TOTP is: ${generator.now()} guess is $guess")
        return generator.verify(guess)
    }

    fun getUrl(accountName: String?) = generator.getURI("QuinnBastCalian", accountName)
}