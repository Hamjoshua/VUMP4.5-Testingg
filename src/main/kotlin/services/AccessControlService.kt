package org.example.AccessControl.services

import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.example.AccessControl.validators.concrete.*
import org.springframework.stereotype.Service

@Service
class AccessControlService(
    private val dataQueryValidator: DataQueryValidator,  // Spring внедрит все зависимости
    private val authValidator: AuthValidator,
    private val resourceFormatValidator: ResourceFormatValidator,
    private val actionValidator: ActionValidator,
    private val resourceExistenceValidator: ResourceExistenceValidator,
    private val permissionValidator: PermissionValidator,
    private val volumeValidator: VolumeValidator
) {

    init {
        // Настраиваем цепочку ответственности
        // Убрали DatabaseConnectionValidator - он не нужен
        dataQueryValidator
            .setNext(authValidator)
            .setNext(resourceFormatValidator)
            .setNext(actionValidator)
            .setNext(resourceExistenceValidator)
            .setNext(permissionValidator)
            .setNext(volumeValidator)
    }

    fun checkAccess(
        validationContext: ValidationContext
    ): StatusCode {
        return when (val result = dataQueryValidator.handle(validationContext)) {
            is ValidationResult.Success -> StatusCode.SUCCESS
            is ValidationResult.Failure -> result.exitCode
        }
    }
}