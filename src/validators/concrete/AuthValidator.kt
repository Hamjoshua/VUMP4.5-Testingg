package org.example.validators.concrete

import org.example.entities.StatusCode
import org.example.entities.User
import org.example.functions.hashPassword
import org.example.validators.BaseValidator
import org.example.validators.ValidationContext
import org.example.validators.ValidationResult


class AuthValidator : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        val users : List<User> = context.usersRepo.getAll()

        val user = users.find{it.login == context.login} ?: return ValidationResult.Failure(StatusCode.INVALID_LOGIN)
        val hashedInput = hashPassword(context.password, user.salt)
        if (hashedInput != user.passwordHash) {
            return ValidationResult.Failure(StatusCode.INVALID_PASSWORD)
        }
        context.user = user
        return ValidationResult.Success
    }
}