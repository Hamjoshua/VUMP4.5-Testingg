package org.example.AccessControl.validators

interface IValidator {
    fun setNext(validator: IValidator) : IValidator
    fun handle(context: ValidationContext) : ValidationResult
}