package services

import data.jpa_repos.PermissionRepository
import data.jpa_repos.ResourceRepository
import data.jpa_repos.UserRepository
import org.example.AccessControl.entities.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccessControlJpaService(
    private val userRepository: UserRepository,
    private val resourceRepository: ResourceRepository,
    private val permissionRepository: PermissionRepository
) {

    fun getUserWithPermissions(login: String): User? {
        return userRepository.findByLogin(login)
    }

    fun checkAccess(login: String, resourcePath: String, action: ResourceAction): Boolean {
        val permission = permissionRepository.findByUserLoginAndResourcePath(login, resourcePath)
        return when (action) {
            ResourceAction.READ -> permission?.canRead ?: false
            ResourceAction.WRITE -> permission?.canWrite ?: false
            ResourceAction.EXECUTE -> permission?.canExecute ?: false
        }
    }

    fun createUser(login: String, passwordHash: String, salt: String): User {
        val user = User(login = login, passwordHash = passwordHash, salt = salt)
        return userRepository.save(user)
    }

    fun createResource(path: String, maxVolume: Int, parentPath: String? = null): Resource {
        val parent = parentPath?.let { resourceRepository.findByPath(it) }
        val resource = Resource(path = path, maxVolume = maxVolume, parent = parent)
        return resourceRepository.save(resource)
    }
}