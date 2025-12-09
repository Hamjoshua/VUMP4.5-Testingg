package org.example.validators.concrete

import org.example.entities.ResourceAction
import org.example.entities.StatusCode
import org.example.validators.BaseValidator
import org.example.validators.ValidationContext
import org.example.validators.ValidationResult

class ActionValidator : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        if (context.action !in setOf("READ", "WRITE", "EXECUTE")) {
            return ValidationResult.Failure(StatusCode.UNKNOWN_ACTION)
        }
        context.requiredResourceAction = ResourceAction.valueOf(context.action)
        return ValidationResult.Success
    }
}