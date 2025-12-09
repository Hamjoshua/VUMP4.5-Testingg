package org.example.AccessControl.validators.concrete

import org.example.AccessControl.entities.Permission
import org.example.AccessControl.entities.ResourceAction
import org.example.AccessControl.entities.StatusCode
import org.example.AccessControl.validators.BaseValidator
import org.example.AccessControl.validators.ValidationContext
import org.example.AccessControl.validators.ValidationResult
import org.springframework.stereotype.Service

@Service
class PermissionValidator: BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        val permissions: List<Permission> = context.permissionsRepo!!.getAll()

        val userLogin = context.user?.login ?:
            return ValidationResult.Failure(StatusCode.INVALID_LOGIN)
        val action = context.requiredResourceAction
        val resourcePath = context.targetResource!!.path

        if (hasAccess(userLogin, resourcePath, action!!, permissions)) {
            return ValidationResult.Success
        }
        return ValidationResult.Failure(StatusCode.NO_ACCESS)
    }

    private fun hasAccess(
        userLogin: String,
        resourcePath: String,
        action: ResourceAction,
        permissions: List<Permission>
    ): Boolean {
        val parts = resourcePath.split(".")
        var current = ""
        for (part in parts) {
            current = if (current.isEmpty()) part else "$current.$part"
            val perm = permissions.find { it.userLogin == userLogin && it.resourcePath == current }
            if (perm != null && perm.actions.contains(action)) return true
        }
        return false
    }
}