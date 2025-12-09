package org.example.validators.concrete

import org.example.data.sqlite_repos.*
import org.example.entities.StatusCode
import org.example.validators.BaseValidator
import org.example.validators.ValidationContext
import org.example.validators.ValidationResult
import org.springframework.stereotype.Service

@Service
class SqliteDbValidator(
    val dbFilePath : String
) : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        try {
            context.resourcesRepo = SqliteResourceRepo(dbFilePath)
            context.permissionsRepo = SqlitePermissionsRepo(dbFilePath)
            context.usersRepo = SqliteUsersRepo(dbFilePath)
        }
        catch (e : Exception){
            return ValidationResult.Failure(exitCode = StatusCode.DB_CONNECTION)
        }
        return ValidationResult.Success
    }
}