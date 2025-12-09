package org.example.entities

data class User(val id: Int,
                val login: String,
                val passwordHash: String,
                val salt: String)
