package org.example.AccessControl.validators.concrete

import org.example.AccessControl.entities.ResourceAction
import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.BaseValidator
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.springframework.stereotype.Service

@Service
class ActionValidator : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        if (context.action !in setOf("READ", "WRITE", "EXECUTE")) {
            return ValidationResult.Failure(StatusCode.UNKNOWN_ACTION)
        }
        context.requiredResourceAction = ResourceAction.valueOf(context.action)
        return ValidationResult.Success
    }
}