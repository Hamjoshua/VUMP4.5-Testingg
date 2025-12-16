package org.example.AccessControl.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users", indexes = [Index(name = "idx_login", columnList = "login", unique = true)])
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(nullable = false, unique = true, length = 50)
    val login: String,

    @Column(name = "password_hash", nullable = false, length = 255)
    val passwordHash: String,

    @Column(nullable = false, length = 32)
    val salt: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val permissions: Set<Permission> = emptySet()
)