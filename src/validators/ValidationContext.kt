package org.example.validators

import org.example.data.interfaces.IPermissionsRepo
import org.example.data.interfaces.IResourcesRepo
import org.example.data.interfaces.IUsersRepo
import org.example.entities.Permission
import org.example.entities.Resource
import org.example.entities.ResourceAction
import org.example.entities.User

// может меняться в зависимости от того, что нужно валидаторам
data class ValidationContext(
    var login: String,
    var password: String,
    var resourcePath: String,
    var action: String, // или enum Action { READ, WRITE, EXECUTE }
    var volume: Int,

    // Дополнительно — для внутреннего использования в валидаторах
    var user: User? = null,        // после авторизации
    var requiredResourceAction: ResourceAction? = null,
    var targetResource: Resource? = null, // после поиска ресурса
    var permission: Set<ResourceAction>? = null,   // после проверки прав

    // пристанище репозиторев. пока тут
    var usersRepo: IUsersRepo?  = null,
    var resourcesRepo: IResourcesRepo? = null,
    var permissionsRepo: IPermissionsRepo? = null
)
