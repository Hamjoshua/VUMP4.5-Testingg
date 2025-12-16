package org.example.AccessControl.validators.concrete

import data.jpa_repos.ResourceRepository
import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.BaseValidator
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.springframework.stereotype.Service

@Service
class ResourceExistenceValidator(
    private val resourceRepository: ResourceRepository  // Внедряем репозиторий
) : BaseValidator() {

    override fun handleSelf(context: ValidationContext): ValidationResult {
        // Используем Spring Data JPA репозиторий
        val resource = resourceRepository.findByPath(context.resourcePath)

        if (resource == null) {
            return ValidationResult.Failure(StatusCode.NON_EXISTENT_RESOURCE)
        }

        context.targetResource = resource
        return ValidationResult.Success
    }
}