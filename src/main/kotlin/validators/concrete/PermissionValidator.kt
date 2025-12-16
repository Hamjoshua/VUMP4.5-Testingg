package org.example.AccessControl.validators.concrete

import data.jpa_repos.PermissionRepository
import org.example.AccessControl.entities.ResourceAction
import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.BaseValidator
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.springframework.stereotype.Service


@Service
class PermissionValidator(
    private val permissionRepository: PermissionRepository  // Внедряем репозиторий
) : BaseValidator() {

    override fun handleSelf(context: ValidationContext): ValidationResult {
        val user = context.user ?:
        return ValidationResult.Failure(StatusCode.INVALID_LOGIN)
        val action = context.requiredResourceAction ?:
        return ValidationResult.Failure(StatusCode.UNKNOWN_ACTION)
        val resource = context.targetResource ?:
        return ValidationResult.Failure(StatusCode.NON_EXISTENT_RESOURCE)

        if (hasAccess(user.login, resource.path, action)) {
            return ValidationResult.Success
        }
        return ValidationResult.Failure(StatusCode.NO_ACCESS)
    }

    private fun hasAccess(
        userLogin: String,
        resourcePath: String,
        action: ResourceAction
    ): Boolean {
        val parts = resourcePath.split(".")
        var current = ""

        for (part in parts) {
            current = if (current.isEmpty()) part else "$current.$part"
            val permission = permissionRepository.findByUserLoginAndResourcePath(userLogin, current)

            if (permission != null && checkAction(permission, action)) {
                return true
            }
        }
        return false
    }

    private fun checkAction(
        permission: org.example.AccessControl.entities.Permission,
        action: ResourceAction
    ): Boolean {
        return when (action) {
            ResourceAction.READ -> permission.canRead
            ResourceAction.WRITE -> permission.canWrite
            ResourceAction.EXECUTE -> permission.canExecute
        }
    }
}