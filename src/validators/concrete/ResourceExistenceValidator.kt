package org.example.validators.concrete

import org.example.entities.Resource
import org.example.entities.StatusCode
import org.example.validators.BaseValidator
import org.example.validators.ValidationContext
import org.example.validators.ValidationResult

class ResourceExistenceValidator(
    private val allResources: List<Resource>
) : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        val resource = allResources.find { it.path == context.targetResource!!.path }
        if (resource == null) {
            return ValidationResult.Failure(StatusCode.NON_EXISTENT_RESOURCE) // Несуществующий ресурс
        }
        context.targetResource = resource
        return ValidationResult.Success
    }
}