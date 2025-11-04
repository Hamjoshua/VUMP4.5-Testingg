package org.example.validators

import org.example.entities.Resource
import org.example.entities.ResourceAction
import org.example.entities.User

// может меняться в зависимости от того, что нужно валидаторам
data class ValidationContext(
    val login: String,
    val password: String,
    val resourcePath: String,
    val action: String, // или enum Action { READ, WRITE, EXECUTE }
    val volume: Int,

    // Дополнительно — для внутреннего использования в валидаторах
    var user: User? = null,        // после авторизации
    var requiredResourceAction: ResourceAction? = null,
    var targetResource: Resource? = null, // после поиска ресурса
    var permission: Set<ResourceAction>? = null   // после проверки прав
)
