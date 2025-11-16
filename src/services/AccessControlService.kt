package org.example.services

import org.example.entities.Permission
import org.example.entities.Resource
import org.example.entities.StatusCode
import org.example.entities.User
import org.example.validators.ValidationContext
import org.example.validators.ValidationResult
import org.example.validators.concrete.ActionValidator
import org.example.validators.concrete.AuthValidator
import org.example.validators.concrete.PermissionValidator
import org.example.validators.concrete.ResourceExistenceValidator
import org.example.validators.concrete.ResourceFormatValidator
import org.example.validators.concrete.VolumeValidator
import org.example.mock.MockData
import org.example.validators.concrete.SqliteDbValidator

class AccessControlService(
    val dbFilePath : String = ""
) {
    private val validatorChain by lazy {
        SqliteDbValidator(dbFilePath)
            .setNext(AuthValidator())
            .setNext(ResourceFormatValidator())
            .setNext(ActionValidator())
            .setNext(ResourceExistenceValidator())
            .setNext(PermissionValidator())
            .setNext(VolumeValidator())
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