package org.example.AccessControl.validators.concrete

import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.BaseValidator
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.springframework.stereotype.Service

@Service
class DataQueryValidator : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        try {
            // TODO Можно проверить каждый метод, чтобы лучше работало
            val vals = context.resourcesRepo!!.getAll()
            context.usersRepo!!.getAll()
            context.permissionsRepo!!.getAll()
        }
        catch (e : Exception){
            return ValidationResult.Failure(StatusCode.INVALID_SQL_QUERY)
        }

        return ValidationResult.Success
    }
}