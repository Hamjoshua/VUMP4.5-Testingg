package org.example.AccessControl.validators

import data.interfaces.IPermissionsRepo
import data.interfaces.IResourcesRepo
import data.interfaces.IUsersRepo
import org.example.AccessControl.entities.Resource
import org.example.AccessControl.entities.ResourceAction
import org.example.AccessControl.entities.User

// Контекст валидации без репозиториев (они будут внедрены в валидаторы)
data class ValidationContext(
    var login: String,
    var password: String,
    var resourcePath: String,
    var action: String,
    var volume: Int,

    // Для внутреннего использования в валидаторах
    var user: User? = null,
    var requiredResourceAction: ResourceAction? = null,
    var targetResource: Resource? = null,
    var userPermissions: Set<ResourceAction>? = null
)