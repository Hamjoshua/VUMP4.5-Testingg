package org.example.validators.concrete

import org.example.entities.Permission
import org.example.entities.ResourceAction
import org.example.entities.StatusCode
import org.example.validators.BaseValidator
import org.example.validators.ValidationContext
import org.example.validators.ValidationResult

class PermissionValidator(
    private val permissions: List<Permission>
) : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
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