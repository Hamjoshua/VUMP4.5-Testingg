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

    //region AuthValidator
    @Test
    fun `AuthValidator - valid credentials should succeed`() {
        val users = listOf(User(id = 1, login = "alice", passwordHash = hashPassword("secret", "salt123"), salt = "salt123"))
        val usersRepo = mockk<IUsersRepo>()
        every { usersRepo.getAll() } returns users

        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 100
        ).apply {
            this.usersRepo = usersRepo
        }

        val validator = AuthValidator()
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Success)
        assertEquals("alice", context.user?.login)
    }

    @Test
    fun `AuthValidator - invalid password should fail`() {
        val users = listOf(User(id = 1, login = "alice", passwordHash = hashPassword("secret", "salt123"), salt = "salt123"))
        val usersRepo = mockk<IUsersRepo>()
        every { usersRepo.getAll() } returns users

        val context = ValidationContext(
            login = "alice", password = "wrong", resourcePath = "A.B", action = "READ", volume = 100
        ).apply {
            this.usersRepo = usersRepo
        }

        val result = AuthValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.INVALID_PASSWORD, (result as ValidationResult.Failure).exitCode)
    }

    @Test
    fun `AuthValidator - unknown user should fail`() {
        val users = listOf(User(id = 1, login = "alice", passwordHash = hashPassword("secret", "salt123"), salt = "salt123"))
        val usersRepo = mockk<IUsersRepo>()
        every { usersRepo.getAll() } returns users

        val context = ValidationContext(
            login = "bob", password = "secret", resourcePath = "A.B", action = "READ", volume = 100
        ).apply {
            this.usersRepo = usersRepo
        }

        val result = AuthValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.INVALID_LOGIN, (result as ValidationResult.Failure).exitCode)
    }
    //endregion

    //region ActionValidator
    @Test
    fun `ActionValidator - valid READ should succeed`() {
        val validator = ActionValidator()
        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 100
        )
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Success)
        assertEquals(ResourceAction.READ, context.requiredResourceAction)
    }

    @Test
    fun `ActionValidator - invalid action should fail`() {
        val validator = ActionValidator()
        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "DELETE", volume = 100
        )
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.UNKNOWN_ACTION, (result as ValidationResult.Failure).exitCode)
    }
    //endregion

    //region ResourceFormatValidator
    @Test
    fun `ResourceFormatValidator - valid path A B C should succeed`() {
        val validator = ResourceFormatValidator()
        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B.C", action = "READ", volume = 100
        )
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `ResourceFormatValidator - invalid char in path should fail`() {
        val validator = ResourceFormatValidator()
        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B-C", action = "READ", volume = 100
        )
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.INVALID_FORMAT, (result as ValidationResult.Failure).exitCode)
    }

    @Test
    fun `ResourceFormatValidator - empty segment should fail`() {
        val validator = ResourceFormatValidator()
        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A..C", action = "READ", volume = 100
        )
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.INVALID_FORMAT, (result as ValidationResult.Failure).exitCode)
    }
    //endregion

    //region ResourceExistenceValidator
    @Test
    fun `ResourceExistenceValidator - existing resource should succeed`() {
        val resources = listOf(Resource(id = 1, path = "A.B.C", maxVolume = 1000))
        val resourcesRepo = mockk<IResourcesRepo>()
        every { resourcesRepo.getAll() } returns resources

        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B.C", action = "READ", volume = 100
        ).apply {
            this.resourcesRepo = resourcesRepo
        }

        val validator = ResourceExistenceValidator()
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Success)
        assertEquals("A.B.C", context.targetResource?.path)
        assertEquals(1000, context.targetResource?.maxVolume)
    }

    @Test
    fun `ResourceExistenceValidator - non-existing resource should fail`() {
        val resources = listOf(Resource(id = 1, path = "A.B", maxVolume = 1000))
        val resourcesRepo = mockk<IResourcesRepo>()
        every { resourcesRepo.getAll() } returns resources

        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "X.Y", action = "READ", volume = 100
        ).apply {
            this.resourcesRepo = resourcesRepo
        }

        val validator = ResourceExistenceValidator()
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.NON_EXISTENT_RESOURCE, (result as ValidationResult.Failure).exitCode)
    }
    //endregion

    //region PermissionValidator
    @Test
    fun `PermissionValidator - direct permission should succeed`() {
        val permissions = listOf(
            Permission(id = 1, userLogin = "alice", resourcePath = "A.B", actions = setOf(ResourceAction.READ))
        )
        val permissionsRepo = mockk<IPermissionsRepo>()
        every { permissionsRepo.getAll() } returns permissions

        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 100
        ).apply {
            this.permissionsRepo = permissionsRepo
            user = User(id = 1, login = "alice", passwordHash = "", salt = "")
            targetResource = Resource(id = 1, path = "A.B", maxVolume = 1000)
            requiredResourceAction = ResourceAction.READ
        }

        val validator = PermissionValidator()
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `PermissionValidator - parent permission should succeed`() {
        val permissions = listOf(
            Permission(id = 1, userLogin = "alice", resourcePath = "A", actions = setOf(ResourceAction.WRITE))
        )
        val permissionsRepo = mockk<IPermissionsRepo>()
        every { permissionsRepo.getAll() } returns permissions

        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B.C", action = "WRITE", volume = 100
        ).apply {
            this.permissionsRepo = permissionsRepo
            user = User(id = 1, login = "alice", passwordHash = "", salt = "")
            targetResource = Resource(id = 1, path = "A.B.C", maxVolume = 1000)
            requiredResourceAction = ResourceAction.WRITE
        }

        val result = PermissionValidator().handle(context)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `PermissionValidator - no permission should fail`() {
        val permissions = listOf(
            Permission(id = 1, userLogin = "alice", resourcePath = "A", actions = setOf(ResourceAction.READ))
        )
        val permissionsRepo = mockk<IPermissionsRepo>()
        every { permissionsRepo.getAll() } returns permissions

        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "X.Y", action = "READ", volume = 100
        ).apply {
            this.permissionsRepo = permissionsRepo
            user = User(id = 1, login = "alice", passwordHash = "", salt = "")
            targetResource = Resource(id = 1, path = "X.Y", maxVolume = 1000)
            requiredResourceAction = ResourceAction.READ
        }

        val result = PermissionValidator().handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.NO_ACCESS, (result as ValidationResult.Failure).exitCode)
    }
    //endregion

    //region VolumeValidator
    @Test
    fun `VolumeValidator - valid volume should succeed`() {
        val validator = VolumeValidator()
        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 500
        ).apply {
            targetResource = Resource(id = 1, path = "A.B", maxVolume = 1000)
        }
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `VolumeValidator - volume exceeding limit should fail`() {
        val validator = VolumeValidator()
        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 1500
        ).apply {
            targetResource = Resource(id = 1, path = "A.B", maxVolume = 1000)
        }
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.VOLUME_EXCEEDED, (result as ValidationResult.Failure).exitCode)
    }

    @Test
    fun `VolumeValidator - negative volume should fail`() {
        val validator = VolumeValidator()
        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = -10
        ).apply {
            targetResource = Resource(id = 1, path = "A.B", maxVolume = 1000)
        }
        val result = validator.handle(context)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(StatusCode.INVALID_FORMAT, (result as ValidationResult.Failure).exitCode)
    }
    //endregion

    //region AccessControlService
    @Test
    fun `AccessControlService - full valid request should succeed`() {
        val service = AccessControlService()
        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "A.B", action = "READ", volume = 50
        )
        val statusCode = service.checkAccess(context)
        assertEquals(StatusCode.SUCCESS, statusCode)
    }

    @Test
    fun `AccessControlService - invalid login should fail`() {
        val service = AccessControlService()
        val context = ValidationContext(
            login = "unknown", password = "secret", resourcePath = "A.B", action = "READ", volume = 50
        )
        val statusCode = service.checkAccess(context)
        assertEquals(StatusCode.INVALID_LOGIN, statusCode)
    }

    @Test
    fun `AccessControlService - no permission should fail`() {
        val service = AccessControlService()
        val context = ValidationContext(
            login = "alice", password = "secret", resourcePath = "X.Y.Z", action = "READ", volume = 50
        )
        val statusCode = service.checkAccess(context)
        assertEquals(StatusCode.NO_ACCESS, statusCode)
    }
    //endregion
}