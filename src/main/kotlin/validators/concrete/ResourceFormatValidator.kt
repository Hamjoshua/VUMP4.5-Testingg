package org.example.AccessControl.validators.concrete

import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.BaseValidator
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.springframework.stereotype.Service

@Service
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