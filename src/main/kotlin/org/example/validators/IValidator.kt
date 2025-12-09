package org.example.validators

interface IValidator {
    fun setNext(validator: IValidator) : IValidator
    fun handle(context: ValidationContext) : ValidationResult
}