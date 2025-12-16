package data.jpa_repos

import data.interfaces.IPermissionsRepo
import org.example.AccessControl.entities.Permission
import org.example.AccessControl.entities.ResourceAction
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class JpaPermissionsRepo(
    private val permissionRepository: PermissionRepository,
    private val userRepository: UserRepository,
    private val resourceRepository: ResourceRepository
) : IPermissionsRepo {

    override fun getById(id: Int): Permission? = permissionRepository.findById(id).orElse(null)

    override fun getAll(): List<Permission> = permissionRepository.findAll()

    override fun getByUserId(userId: Int): List<Permission> = permissionRepository.findByUserId(userId)

    @Transactional
    override fun add(permission: Permission): Boolean {
        return try {
            permissionRepository.save(permission)
            true
        } catch (e: Exception) {
            false
        }
    }

    @Transactional
    override fun update(permission: Permission): Boolean {
        return if (permission.id != null && permissionRepository.existsById(permission.id)) {
            permissionRepository.save(permission)
            true
        } else {
            false
        }
    }

    @Transactional
    override fun delete(id: Int): Boolean {
        return if (permissionRepository.existsById(id)) {
            permissionRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    // Дополнительные методы для удобства
    fun findByUserLoginAndResourcePath(userLogin: String, resourcePath: String): Permission? {
        return permissionRepository.findByUserLoginAndResourcePath(userLogin, resourcePath)
    }

    fun createPermission(
        userLogin: String,
        resourcePath: String,
        actions: Set<ResourceAction>
    ): Permission? {
        val user = userRepository.findByLogin(userLogin) ?: return null
        val resource = resourceRepository.findByPath(resourcePath) ?: return null

        val permission = Permission(
            user = user,
            resource = resource,
            canRead = actions.contains(ResourceAction.READ),
            canWrite = actions.contains(ResourceAction.WRITE),
            canExecute = actions.contains(ResourceAction.EXECUTE)
        )

        return permissionRepository.save(permission)
    }
}