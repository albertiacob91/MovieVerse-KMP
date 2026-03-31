package com.albertiacob91.movieversekmp.server.auth

import java.security.MessageDigest

object PasswordHasher {

    fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verify(password: String, hashedPassword: String): Boolean {
        return hash(password) == hashedPassword
    }
}