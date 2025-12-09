package org.example.AccessControl.validators.concrete

import org.example.AccessControl.data.sqlite_repos.SqlitePermissionsRepo
import org.example.AccessControl.data.sqlite_repos.SqliteResourceRepo
import org.example.AccessControl.data.sqlite_repos.SqliteUsersRepo
import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.BaseValidator
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.springframework.stereotype.Service

@Service
class SqliteDbValidator() : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        try {
            context.resourcesRepo = SqliteResourceRepo()
            context.permissionsRepo = SqlitePermissionsRepo()
            context.usersRepo = SqliteUsersRepo()
        }
        catch (e : Exception){
            return ValidationResult.Failure(exitCode = StatusCode.DB_CONNECTION)
        }
        return ValidationResult.Success
    }
}