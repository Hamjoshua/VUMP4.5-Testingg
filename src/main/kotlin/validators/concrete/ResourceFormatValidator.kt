package org.example.validators.concrete

import org.example.entities.StatusCode
import org.example.validators.BaseValidator
import org.example.validators.ValidationContext
import org.example.validators.ValidationResult

class ResourceFormatValidator : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        if (!isValidResourcePath(context.resourcePath)) {  // Некорректный формат ресурса
            return ValidationResult.Failure(StatusCode.INVALID_FORMAT)
        }
        return ValidationResult.Success
    }

    private fun isValidResourcePath(path: String): Boolean {
        return path.split(".").all { name ->
            name.length in 1..20 && name.matches(Regex("^[a-zA-Z0-9_]+\$"))
        }
    }
}