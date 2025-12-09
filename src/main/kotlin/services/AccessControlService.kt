package org.example.AccessControl.services

import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.example.AccessControl.validators.concrete.*
import org.springframework.stereotype.Service

@Service
class AccessControlService() {
    private val validatorChain by lazy {
        val first = SqliteDbValidator()
        first.setNext(DataQueryValidator())
            .setNext(AuthValidator())
            .setNext(ResourceFormatValidator())
            .setNext(ActionValidator())
            .setNext(ResourceExistenceValidator())
            .setNext(PermissionValidator())
            .setNext(VolumeValidator())

        return@lazy first
    }

    fun checkAccess(
        validationContext: ValidationContext
    ): StatusCode {
        return when (val result = validatorChain.handle(validationContext)) {
            is ValidationResult.Success -> StatusCode.SUCCESS
            is ValidationResult.Failure -> result.exitCode
        }
    }
}