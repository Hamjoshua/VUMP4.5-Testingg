package org.example.AccessControl.validators.concrete

import data.jpa_repos.PermissionRepository
import data.jpa_repos.ResourceRepository
import data.jpa_repos.UserRepository
import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.BaseValidator
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DataQueryValidator(
    private val userRepository: UserRepository,
    private val resourceRepository: ResourceRepository,
    private val permissionRepository: PermissionRepository
) : BaseValidator() {

    @Transactional(readOnly = true)
    override fun handleSelf(context: ValidationContext): ValidationResult {
        return try {
            userRepository.count()
            resourceRepository.count()
            permissionRepository.count()
            ValidationResult.Success
        } catch (e: Exception) {
            ValidationResult.Failure(StatusCode.INVALID_SQL_QUERY)
        }
    }
}