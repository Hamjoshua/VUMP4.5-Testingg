package data.jpa_repos

import org.example.AccessControl.entities.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository : JpaRepository<Permission, Int> {
    @Query("SELECT p FROM Permission p WHERE p.user.id = :userId")
    fun findByUserId(userId: Int): List<Permission>

    @Query("SELECT p FROM Permission p WHERE p.user.login = :userLogin")
    fun findByUserLogin(userLogin: String): List<Permission>

    @Query("SELECT p FROM Permission p WHERE p.user.id = :userId AND p.resource.id = :resourceId")
    fun findByUserIdAndResourceId(userId: Int, resourceId: Int): Permission?

    @Query("SELECT p FROM Permission p WHERE p.user.login = :userLogin AND p.resource.path = :resourcePath")
    fun findByUserLoginAndResourcePath(userLogin: String, resourcePath: String): Permission?
}