package org.example.AccessControl.mock
import org.example.entities.Permission
import org.example.entities.Resource
import org.example.entities.ResourceAction
import org.example.entities.User
import functions.hashPassword

object MockData {
    val users = listOf(
        User(
            id = 1,
            login = "alice",
            passwordHash = hashPassword("password123", "salt_alice"),
            salt = "salt_alice"
        ),
        User(
            id = 2,
            login = "bob",
            passwordHash = hashPassword("qwerty", "salt_bob"),
            salt = "salt_bob"
        ),
        User(
            id = 3,
            login = "charlie",
            passwordHash = hashPassword("pass123", "salt_charlie"),
            salt = "salt_charlie"
        )
    )

    val resourceA = Resource(id = 1, path = "A", maxVolume = 100)
    val resourceAB = Resource(id = 2, path = "A.B", maxVolume = 50, parent = resourceA)
    val resourceABC = Resource(id = 3, path = "A.B.C", maxVolume = 20, parent = resourceAB)
    val resourceABD = Resource(id = 4, path = "A.B.D", maxVolume = 30, parent = resourceAB)
    val resourceX = Resource(id = 5, path = "X", maxVolume = 200)
    val resourceXY = Resource(id = 6, path = "X.Y", maxVolume = 75, parent = resourceX)

    val resources = listOf(resourceA, resourceAB, resourceABC, resourceABD, resourceX, resourceXY)

    val permissions = listOf(
        Permission(
            id = 1,
            userLogin = "alice",
            resourcePath = "A.B",
            actions = setOf(ResourceAction.READ, ResourceAction.WRITE, ResourceAction.EXECUTE)
        ),
        Permission(
            id = 2,
            userLogin = "alice",
            resourcePath = "X",
            actions = setOf(ResourceAction.READ)
        ),

        Permission(
            id = 3,
            userLogin = "bob",
            resourcePath = "A.B.C",
            actions = setOf(ResourceAction.READ)
        ),
        Permission(
            id = 4,
            userLogin = "bob",
            resourcePath = "X.Y",
            actions = setOf(ResourceAction.WRITE)
        ),

        Permission(
            id = 5,
            userLogin = "charlie",
            resourcePath = "X",
            actions = setOf(ResourceAction.EXECUTE)
        ),
        Permission(
            id = 6,
            userLogin = "charlie",
            resourcePath = "A.B.D",
            actions = setOf(ResourceAction.READ, ResourceAction.WRITE, ResourceAction.EXECUTE)
        )
    )

    // Вспомогательные методы для тестов
    fun findUserByLogin(login: String): User? = users.find { it.login == login }

    fun findUserById(id: Int): User? = users.find { it.id == id }

    fun findResourceByPath(path: String): Resource? = resources.find { it.path == path }

    fun findResourceById(id: Int): Resource? = resources.find { it.id == id }

    fun getPermissionsForUser(login: String): List<Permission> =
        permissions.filter { it.userLogin == login }

    fun getPermissionsForUserId(userId: Int): List<Permission> {
        val user = findUserById(userId)
        return if (user != null) {
            permissions.filter { it.userLogin == user.login }
        } else {
            emptyList()
        }
    }

    fun getPermissionById(id: Int): Permission? = permissions.find { it.id == id }
}