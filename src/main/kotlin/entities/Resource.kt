package org.example.AccessControl.entities

import jakarta.persistence.*

@Entity
@Table(name = "resources", indexes = [
    Index(name = "idx_path", columnList = "path", unique = true),
    Index(name = "idx_parent", columnList = "parent_id")
])
data class Resource(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(nullable = false, unique = true, length = 500)
    val path: String,

    @Column(name = "max_volume", nullable = false)
    val maxVolume: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val parent: Resource? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val children: Set<Resource> = emptySet(),

    @OneToMany(mappedBy = "resource", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val permissions: Set<Permission> = emptySet(),

    @Column(name = "created_at", nullable = false)
    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
)