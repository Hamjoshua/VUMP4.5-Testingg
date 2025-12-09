package org.example.validators

abstract class BaseValidator : IValidator {
    private var _next : IValidator? = null

    override fun setNext(validator: IValidator): IValidator {
        _next = validator
        return validator
    }

    override fun handle(context: ValidationContext): ValidationResult {
        return when(val result = handleSelf(context)){
            is ValidationResult.Success -> _next?.handle(context) ?: result
            is ValidationResult.Failure -> result
        }
    }

    abstract fun handleSelf(context: ValidationContext) : ValidationResult
}