package org.example.AccessControl.entities


import jakarta.persistence.*

@Entity
@Table(name = "permissions", indexes = [
    Index(name = "idx_user_resource", columnList = "user_id, resource_id", unique = true)
])
data class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    val resource: Resource,

    @Column(name = "can_read", nullable = false)
    val canRead: Boolean = false,

    @Column(name = "can_write", nullable = false)
    val canWrite: Boolean = false,

    @Column(name = "can_execute", nullable = false)
    val canExecute: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
) {
    val actions: Set<ResourceAction>
        get() = buildSet {
            if (canRead) add(ResourceAction.READ)
            if (canWrite) add(ResourceAction.WRITE)
            if (canExecute) add(ResourceAction.EXECUTE)
        }
}