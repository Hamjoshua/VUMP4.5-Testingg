package org.example.functions

import java.security.MessageDigest
import java.util.Base64
import kotlin.random.Random

fun hashPassword(password: String, salt: String) : String {
    val digest = MessageDigest.getInstance("SHA-256")
    val saltedPassword = password + salt
    val hash = digest.digest(saltedPassword.toByteArray())
    return Base64.getEncoder().encodeToString(hash)
}

fun saltGenerator(length: Int = 16): String{
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}