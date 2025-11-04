package org.example.mock
import org.example.entities.Permission
import org.example.entities.Resource
import org.example.entities.ResourceAction
import org.example.entities.User
import org.example.functions.hashPassword

object MockData {
    val users = listOf(
        User(
            login = "alice",
            passwordHash = hashPassword("password123", "salt_alice"),
            salt = "salt_alice"
        ),
        User(
            login = "bob",
            passwordHash = hashPassword("qwerty", "salt_bob"),
            salt = "salt_bob"
        ),
        User(
            login = "charlie",
            passwordHash = hashPassword("pass123", "salt_charlie"),
            salt = "salt_charlie"
        )
    )

    val resourceA = Resource(path = "A", maxVolume = 100)
    val resourceAB = Resource(path = "A.B", maxVolume = 50, parent = resourceA)
    val resourceABC = Resource(path = "A.B.C", maxVolume = 20, parent = resourceAB)
    val resourceABD = Resource(path = "A.B.D", maxVolume = 30, parent = resourceAB)
    val resourceX = Resource(path = "X", maxVolume = 200)
    val resourceXY = Resource(path = "X.Y", maxVolume = 75, parent = resourceX)

    val resources = listOf(resourceA, resourceAB, resourceABC, resourceABD, resourceX, resourceXY)

    val permissions = listOf(
        Permission(
            userLogin = "alice",
            resourcePath = "A.B",
            actions = setOf(ResourceAction.READ, ResourceAction.WRITE, ResourceAction.EXECUTE)
        ),
        Permission(
            userLogin = "alice",
            resourcePath = "X",
            actions = setOf(ResourceAction.READ)
        ),

        Permission(
            userLogin = "bob",
            resourcePath = "A.B.C",
            actions = setOf(ResourceAction.READ)
        ),
        Permission(
            userLogin = "bob",
            resourcePath = "X.Y",
            actions = setOf(ResourceAction.WRITE)
        ),

        Permission(
            userLogin = "charlie",
            resourcePath = "X",
            actions = setOf(ResourceAction.EXECUTE)
        ),
        Permission(
            userLogin = "charlie",
            resourcePath = "A.B.D",
            actions = setOf(ResourceAction.READ, ResourceAction.WRITE, ResourceAction.EXECUTE)
        )
    )

    // Вспомогательные методы для тестов
    fun findUserByLogin(login: String): User? = users.find { it.login == login }

    fun findResourceByPath(path: String): Resource? = resources.find { it.path == path }

    fun getPermissionsForUser(login: String): List<Permission> =
        permissions.filter { it.userLogin == login }
}