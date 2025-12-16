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
        return try {
            val action = ResourceAction.valueOf(context.action.uppercase())
            context.requiredResourceAction = action
            ValidationResult.Success
        } catch (e: IllegalArgumentException) {
            ValidationResult.Failure(StatusCode.UNKNOWN_ACTION)
        }
    }
}