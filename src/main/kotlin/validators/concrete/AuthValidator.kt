package org.example.AccessControl.validators.concrete

import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.entities.User
import org.example.AccessControl.functions.hashPassword
import org.example.AccessControl.validators.BaseValidator
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.springframework.stereotype.Service

@Service
class AuthValidator : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        val users : List<User> = context.usersRepo!!.getAll()

        val user = users.find{it.login == context.login} ?: return ValidationResult.Failure(StatusCode.INVALID_LOGIN)
        val hashedInput = hashPassword(context.password, user.salt)
        if (hashedInput != user.passwordHash) {
            return ValidationResult.Failure(StatusCode.INVALID_PASSWORD)
        }
        context.user = user
        return ValidationResult.Success
    }
}