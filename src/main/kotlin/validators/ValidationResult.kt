package org.example.AccessControl.validators

import org.example.AccessControl.entities.StatusCode

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val exitCode: StatusCode) : ValidationResult()
}