package org.example

import io.mockk.every
import io.mockk.mockk
import org.example.entities.*
import org.example.functions.hashPassword
import org.example.data.interfaces.IPermissionsRepo
import org.example.data.interfaces.IResourcesRepo
import org.example.data.interfaces.IUsersRepo
import org.example.services.AccessControlService
import org.example.validators.ValidationContext
import org.example.validators.ValidationResult
import org.example.validators.concrete.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class AllValidatorsTest {

    // --- Test data factories ---
    fun getUsers(): List<User> =
        listOf(User(id = 1, login = "alice", passwordHash = hashPassword("secret", "salt123"), salt = "salt123"))

    fun getResources(): List<Resource> =
        listOf(Resource(id = 1, path = "A.B", maxVolume = 1000))

    fun getPermissions(): List<Permission> =
        listOf(Permission(id = 1, userLogin = "alice", resourcePath = "A", actions = setOf(ResourceAction.READ)))

    // --- Context builders with mocks ---
    fun createContextWithUsersRepo(users: List<User> = getUsers()): ValidationContext {
        val repo = mockk<IUsersRepo>()
        every { repo.getAll() } returns users
        return ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 100
        ).apply { usersRepo = repo }
    }

    fun createContextWithResourcesRepo(resources: List<Resource> = getResources()): ValidationContext {
        val repo = mockk<IResourcesRepo>()
        every { repo.getAll() } returns resources
        return ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 100
        ).apply { resourcesRepo = repo }
    }

    fun createContextWithPermissionsRepo(
        permissions: List<Permission> = getPermissions(),
        user: User = User(id = 1, login = "alice", passwordHash = "", salt = ""),
        targetResource: Resource = Resource(id = 1, path = "A.B", maxVolume = 1000),
        requiredAction: ResourceAction = ResourceAction.READ
    ): ValidationContext {
        val repo = mockk<IPermissionsRepo>()
        every { repo.getAll() } returns permissions
        return ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 100
        ).apply {
            permissionsRepo = repo
            this.user = user
            this.targetResource = targetResource
            requiredResourceAction = requiredAction
        }
    }

    // --- AuthValidator ---
    @Test
    fun `AuthValidator - valid credentials should succeed`() {
        val context = createContextWithUsersRepo().apply { this.password = "secret"; this.login = "alice" }
        val result = AuthValidator().handle(context)
        assertTrue(result is ValidationResult.Success)
        assertEquals("alice", context.user?.login)
    }

    @Test
    fun `AuthValidator - invalid password should fail`() {
        val context = createContextWithUsersRepo().apply { this.password = "wrong"; this.login = "alice" }
        val result = AuthValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.INVALID_PASSWORD, (result as ValidationResult.Failure).exitCode)
    }

    @Test
    fun `AuthValidator - unknown user should fail`() {
        val context = createContextWithUsersRepo().apply { this.password = "secret"; this.login = "bob" }
        val result = AuthValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.INVALID_LOGIN, (result as ValidationResult.Failure).exitCode)
    }

    // --- ActionValidator (no duplication) ---
    @Test
    fun `ActionValidator - valid READ should succeed`() {
        val context = ValidationContext(login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 100)
        val result = ActionValidator().handle(context)
        assertTrue(result is ValidationResult.Success)
        assertEquals(ResourceAction.READ, context.requiredResourceAction)
    }

    @Test
    fun `ActionValidator - invalid action should fail`() {
        val context = ValidationContext(login = "alice", password = "secret", resourcePath = "A.B", action = "DELETE", volume = 100)
        val result = ActionValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.UNKNOWN_ACTION, (result as ValidationResult.Failure).exitCode)
    }

    // --- ResourceFormatValidator (no duplication) ---
    @Test
    fun `ResourceFormatValidator - valid path A B C should succeed`() {
        val context = ValidationContext(login = "alice", password = "secret", resourcePath = "A.B.C", action = "READ", volume = 100)
        assertTrue(ResourceFormatValidator().handle(context) is ValidationResult.Success)
    }

    @Test
    fun `ResourceFormatValidator - invalid char in path should fail`() {
        val context = ValidationContext(login = "alice", password = "secret", resourcePath = "A.B-C", action = "READ", volume = 100)
        val result = ResourceFormatValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.INVALID_FORMAT, (result as ValidationResult.Failure).exitCode)
    }

    @Test
    fun `ResourceFormatValidator - empty segment should fail`() {
        val context = ValidationContext(login = "alice", password = "secret", resourcePath = "A..C", action = "READ", volume = 100)
        val result = ResourceFormatValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.INVALID_FORMAT, (result as ValidationResult.Failure).exitCode)
    }

    // --- ResourceExistenceValidator ---
    @Test
    fun `ResourceExistenceValidator - existing resource should succeed`() {
        val resources = listOf(Resource(id = 1, path = "A.B.C", maxVolume = 1000))
        val context = createContextWithResourcesRepo(resources).apply { resourcePath = "A.B.C" }
        val result = ResourceExistenceValidator().handle(context)
        assertTrue(result is ValidationResult.Success)
        assertEquals("A.B.C", context.targetResource?.path)
        assertEquals(1000, context.targetResource?.maxVolume)
    }

    @Test
    fun `ResourceExistenceValidator - non-existing resource should fail`() {
        val resources = listOf(Resource(id = 1, path = "A.B", maxVolume = 1000))
        val context = createContextWithResourcesRepo(resources).apply { resourcePath = "X.Y" }
        val result = ResourceExistenceValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.NON_EXISTENT_RESOURCE, (result as ValidationResult.Failure).exitCode)
    }

    // --- PermissionValidator ---
    @Test
    fun `PermissionValidator - direct permission should succeed`() {
        val permissions = listOf(Permission(id = 1, userLogin = "alice", resourcePath = "A.B", actions = setOf(ResourceAction.READ)))
        val context = createContextWithPermissionsRepo(
            permissions = permissions,
            targetResource = Resource(id = 1, path = "A.B", maxVolume = 1000),
            requiredAction = ResourceAction.READ
        ).apply { resourcePath = "A.B"; action = "READ"; requiredResourceAction = ResourceAction.READ }
        assertTrue(PermissionValidator().handle(context) is ValidationResult.Success)
    }

    @Test
    fun `PermissionValidator - parent permission should succeed`() {
        val permissions = listOf(Permission(id = 1, userLogin = "alice", resourcePath = "A", actions = setOf(ResourceAction.WRITE)))
        val context = createContextWithPermissionsRepo(
            permissions = permissions,
            targetResource = Resource(id = 1, path = "A.B.C", maxVolume = 1000),
            requiredAction = ResourceAction.WRITE
        ).apply { resourcePath = "A.B.C"; action = "WRITE"; requiredResourceAction = ResourceAction.WRITE }
        assertTrue(PermissionValidator().handle(context) is ValidationResult.Success)
    }

    @Test
    fun `PermissionValidator - no permission should fail`() {
        val permissions = listOf(Permission(id = 1, userLogin = "alice", resourcePath = "A", actions = setOf(ResourceAction.READ)))
        val context = createContextWithPermissionsRepo(
            permissions = permissions,
            targetResource = Resource(id = 1, path = "X.Y", maxVolume = 1000),
            requiredAction = ResourceAction.READ
        ).apply { resourcePath = "X.Y"; action = "READ"; requiredResourceAction = ResourceAction.READ }
        val result = PermissionValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.NO_ACCESS, (result as ValidationResult.Failure).exitCode)
    }

    // --- VolumeValidator ---
    @Test
    fun `VolumeValidator - valid volume should succeed`() {
        val context = ValidationContext(login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 500)
            .apply { targetResource = Resource(id = 1, path = "A.B", maxVolume = 1000) }
        assertTrue(VolumeValidator().handle(context) is ValidationResult.Success)
    }

    @Test
    fun `VolumeValidator - volume exceeding limit should fail`() {
        val context = ValidationContext(login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 1500)
            .apply { targetResource = Resource(id = 1, path = "A.B", maxVolume = 1000) }
        val result = VolumeValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.VOLUME_EXCEEDED, (result as ValidationResult.Failure).exitCode)
    }

    @Test
    fun `VolumeValidator - negative volume should fail`() {
        val context = ValidationContext(login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = -10)
            .apply { targetResource = Resource(id = 1, path = "A.B", maxVolume = 1000) }
        val result = VolumeValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.INVALID_FORMAT, (result as ValidationResult.Failure).exitCode)
    }

    // --- AccessControlService (оставляем как есть — это интеграционные тесты) ---
    @Test
    fun `AccessControlService - full valid request should succeed`() {
        val service = AccessControlService("db/database.db") // ← исправлен путь!
        val context = ValidationContext(login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 50)
        assertEquals(StatusCode.SUCCESS, service.checkAccess(context))
    }

    @Test
    fun `AccessControlService - invalid login should fail`() {
        val service = AccessControlService("db/database.db")
        val context = ValidationContext(login = "unknown", password = "secret", resourcePath = "A.B", action = "READ", volume = 50)
        assertEquals(StatusCode.INVALID_LOGIN, service.checkAccess(context))
    }

    @Test
    fun `AccessControlService - no permission should fail`() {
        val service = AccessControlService("db/database.db")
        val context = ValidationContext(login = "alice", password = "secret", resourcePath = "X.Y.Z", action = "READ", volume = 50)
        assertEquals(StatusCode.NO_ACCESS, service.checkAccess(context))
    }
}