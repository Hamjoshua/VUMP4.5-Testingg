package org.example.AccessControl.validators.concrete

import org.example.AccessControl.entities.Resource
import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.BaseValidator
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.springframework.stereotype.Service

@Service
class ResourceExistenceValidator : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        val allResources : List<Resource> = context.resourcesRepo!!.getAll()

        val resource = allResources.find { it.path == context.resourcePath }
        if (resource == null) {
            return ValidationResult.Failure(StatusCode.NON_EXISTENT_RESOURCE) // Несуществующий ресурс
        }
        context.targetResource = resource
        return ValidationResult.Success
    }
}