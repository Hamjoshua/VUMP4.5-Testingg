package org.example.validators

import org.example.entities.StatusCode

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val exitCode: StatusCode) : ValidationResult()
}