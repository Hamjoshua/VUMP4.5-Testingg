package org.example.validators.concrete

import org.example.entities.StatusCode
import org.example.validators.BaseValidator
import org.example.validators.ValidationContext
import org.example.validators.ValidationResult

class VolumeValidator : BaseValidator() {
    override fun handleSelf(context: ValidationContext): ValidationResult {
        val resource = context.targetResource ?:
            return ValidationResult.Failure(StatusCode.NON_EXISTENT_RESOURCE)
        if (context.volume > resource.maxVolume) { // Превышение объёма
            return ValidationResult.Failure(StatusCode.VOLUME_EXCEEDED)
        }
        if (context.volume < 0) { // Некорректный объём
            return ValidationResult.Failure(StatusCode.INVALID_FORMAT)
        }
        return ValidationResult.Success
    }
}