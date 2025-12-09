package org.example.AccessControl.validators.concrete

import org.example.entities.AccessControl.ResourceAction
import org.example.entities.AccessControl.StatusCode
import org.example.validators.AccessControl.BaseValidator
import org.example.validators.AccessControl.ValidationContext
import org.example.validators.AccessControl.ValidationResult
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