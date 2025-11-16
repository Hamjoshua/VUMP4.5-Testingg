package org.example.services

import org.example.entities.StatusCode
import org.example.validators.ValidationContext
import org.example.validators.ValidationResult
import org.example.validators.concrete.*

class AccessControlService(
    val dbFilePath : String = "db\\database1.db"
) {
    private val validatorChain by lazy {
        val first = SqliteDbValidator(dbFilePath)
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